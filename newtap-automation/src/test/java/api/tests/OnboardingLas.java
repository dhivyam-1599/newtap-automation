package api.tests;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import api.utilities.TestDataProviders;
import api.utilities.database.DbQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static api.utilities.PayloadUtils.*;


public class OnboardingLas {


    public static Response createBorrower() throws IOException, InterruptedException {
        String createJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")));
        createJson = createJson.replace("{{reference_id}}", PayloadUtils.referenceID);
        Response response = Service.createBorrower(createJson);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        Thread.sleep(15000);
        return response;
    }
    public static Response creditReportUpdate() throws IOException, InterruptedException {
        DbQuery dbQuery = new DbQuery();
        String requestID = dbQuery.getRequestId(las_crn);  // fetch request ID here

        if (requestID == null) {
            throw new RuntimeException("No request ID found for CRN: " + las_crn);
        }

        String creditJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/creditreport.json")));
        creditJson = creditJson.replace("{{crn}}", las_crn)
                .replace("{{requestid}}", requestID);

        Response response = Service.creditReportUpdate(creditJson);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        return response;
    }


    public static void createBorrowerAndVerifyStatus() throws IOException, InterruptedException {
        DbQuery dBquery = new DbQuery();
        Response createResponse = createBorrower();
        String ref_id = createResponse.jsonPath().getString("data.id");
        Response statusResponse = Service.createBorrowerStatus(ref_id);
        las_appform = statusResponse.jsonPath().getString("data.borrower_data.reference_id");
        las_crn = statusResponse.jsonPath().getString("data.borrower_data.crn");
        statusResponse.then().log().all();
        System.out.println("Switch to CRED STAGE VPN");
        TimeUnit.MINUTES.sleep(1);
        dBquery.getUserDetails(las_crn);
        creditReportUpdate();
        System.out.println("Switch to NEWTAP UAT VPN");
        TimeUnit.MINUTES.sleep(1);
        Assert.assertEquals(statusResponse.getStatusCode(), 200);
    }

    public static String getPresignedUrl(String objectPath) throws IOException {
        String requestJson = objectPath ;
        Response getResponse = Service.getPresignedUrls(requestJson);
        getResponse.then().log().all();
        Assert.assertEquals(getResponse.getStatusCode(), 200);
        return getResponse.jsonPath().getString("data");
    }

    @Test
    public void testCreateBorrower() throws IOException, InterruptedException {
        createBorrower();
    }
    @Test
    public void checkCreateBorrowerStatus() throws IOException, InterruptedException {
        createBorrowerAndVerifyStatus();
    }
    @Test
    public void lasOnboarding() throws IOException, InterruptedException {
        createBorrowerAndVerifyStatus();
        Map<String, String> payload = PayloadUtils.jsonHelper();
        String updateJson = payload.get("updateJson");
        Response updateresponse = Service.updateBorrower(updateJson);
        updateresponse.then().log().all();
        Assert.assertEquals(updateresponse.getStatusCode(), 200);
    }

    @Test(dataProvider = "invalidPANDetails", dataProviderClass = TestDataProviders.class)
    public void testInvalidPANDetails(String panNumber, String panName, String reason) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(
                new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")))
        );
        ObjectNode panDetail = (ObjectNode) root.path("data").path("pan_detail");
        panDetail.put("pan_number", panNumber);
        panDetail.put("pan_name", panName);
        String invalidJson = mapper.writeValueAsString(root);
        Response response = Service.createBorrower(invalidJson);
        response.then().log().all();
        Assert.assertTrue(
                response.getStatusCode() == 200 || response.getStatusCode() == 422,
                "Expected validation error for: " + reason + " but got " + response.getStatusCode()
        );
    }

    @Test(dataProvider = "invalidPANnumberPANname", dataProviderClass = TestDataProviders.class)
    public void testPanAadharLinkage(String panNumber, String panName, String reason) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(
                new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")))
        );
        ObjectNode panDetail = (ObjectNode) root.path("data").path("pan_detail");

        panDetail.put("pan_number", panNumber);
        panDetail.put("pan_name", panName);
        String invalidJson = mapper.writeValueAsString(root);
        Response response = Service.createBorrower(invalidJson);
        response.then().log().all();
        Assert.assertTrue(
                response.getStatusCode() == 200 || response.getStatusCode() == 422,
                "Expected validation error for: " + reason + " but got " + response.getStatusCode()
        );
    }

    @Test(dataProvider = "invalidUserdetails", dataProviderClass = TestDataProviders.class)
    public void testInvalidUserdetails(String userID, String PhnNo,String UserName,String Email, String reason) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(
                new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")))
        );
        ObjectNode UserDetail = (ObjectNode) root.path("data").path("user_detail");

        UserDetail.put("user_id", userID);
        UserDetail.put("phone_no", PhnNo);
        UserDetail.put("user_name", UserName);
        UserDetail.put("user_email", Email);

        String invalidJson = mapper.writeValueAsString(root);
        Response response = Service.createBorrower(invalidJson);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP 200");

        boolean success = response.jsonPath().getBoolean("success");
        int errorCode = response.jsonPath().getInt("error_code");
        Assert.assertFalse(success, "Expected success=false but got true");
        Assert.assertNotEquals(errorCode, 0, "Expected non-zero error code for invalid PAN");
    }


}
