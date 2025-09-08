package api.tests;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import api.utilities.TestDataProviders;
import api.utilities.database.DBquery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import static api.utilities.PayloadUtils.las_appform;
import static api.utilities.PayloadUtils.las_crn;

public class onboarding_las {


    public static Response CreateBorrower() throws IOException, InterruptedException {
        String createJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")));
        createJson = createJson.replace("{{reference_id}}", PayloadUtils.referenceID);
        Response response = Service.createborrower(createJson);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        Thread.sleep(15000);
        return response;
    }

    public static void CreateBorrowerAndVerifyStatus() throws IOException, InterruptedException {
        DBquery dBquery = new DBquery();
        Response createResponse = CreateBorrower();
        String ref_id = createResponse.jsonPath().getString("data.id");
        Response statusResponse = Service.createborrowerstatus(ref_id);
        las_appform = statusResponse.jsonPath().getString("data.borrower_data.reference_id");
        las_crn = statusResponse.jsonPath().getString("data.borrower_data.crn");
        statusResponse.then().log().all();
        System.out.println("Switch to CRED STAGE VPN");
        Thread.sleep(15000);
        dBquery.getuserdetails(las_crn);
        System.out.println("Switch to NEWTAP UAT VPN");
        Thread.sleep(15000);
        Assert.assertEquals(statusResponse.getStatusCode(), 200);
    }

    public static String GetPresignedUrl(String objectPath) throws IOException {
        String requestJson = objectPath ;
        Response getResponse = Service.getpresignedurls(requestJson);
        getResponse.then().log().all();
        Assert.assertEquals(getResponse.getStatusCode(), 200);
        return getResponse.jsonPath().getString("data");
    }

    @Test
    public void testCreateBorrower() throws IOException, InterruptedException {
        CreateBorrower();
    }
    @Test
    public void checkCreateBorrowerStatus() throws IOException, InterruptedException {
        CreateBorrowerAndVerifyStatus();
    }
    @Test
    public void lasOnboarding() throws IOException, InterruptedException {
        CreateBorrowerAndVerifyStatus();
        Map<String, String> payload = PayloadUtils.JsonHelper();
        String updateJson = payload.get("updateJson");
        Response updateresponse = Service.updateborrower(updateJson);
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
        Response response = Service.createborrower(invalidJson);
        response.then().log().all();
        Assert.assertTrue(
                response.getStatusCode() == 400 || response.getStatusCode() == 422,
                "Expected validation error for: " + reason + " but got " + response.getStatusCode()
        );
    }

    @Test(dataProvider = "invalidPANAadhar", dataProviderClass = TestDataProviders.class)
    public void testPanAadharLinkage(String panNumber, String panName, String reason) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(
                new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")))
        );
        ObjectNode panDetail = (ObjectNode) root.path("data").path("pan_detail");

        panDetail.put("pan_number", panNumber);
        panDetail.put("pan_name", panName);
        String invalidJson = mapper.writeValueAsString(root);
        Response response = Service.createborrower(invalidJson);
        response.then().log().all();
        Assert.assertTrue(
                response.getStatusCode() == 400 || response.getStatusCode() == 422,
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
        Response response = Service.createborrower(invalidJson);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Expected HTTP 200");

        boolean success = response.jsonPath().getBoolean("success");
        int errorCode = response.jsonPath().getInt("error_code");
        Assert.assertFalse(success, "Expected success=false but got true");
        Assert.assertNotEquals(errorCode, 0, "Expected non-zero error code for invalid PAN");
    }


}
