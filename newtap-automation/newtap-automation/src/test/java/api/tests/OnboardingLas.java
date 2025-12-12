package api.tests;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import static api.helpers.OnboardingHelper.createBorrower;
import static api.helpers.OnboardingHelper.createBorrowerAndVerifyStatus;



public class OnboardingLas {

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
    @Test
    public void PanCheck() throws IOException,InterruptedException {
        PayloadUtils utils = new PayloadUtils();
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")));
        payload = payload.replace("{{reference_id}}", utils.referenceID);

        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("data").getJSONObject("pan_detail")
                .put("pan_number","ABCGYJK4678");
        payload = jsonObject.toString();

        Response tresponse = Service.createBorrower(payload);

        int code = tresponse.getStatusCode();
        System.out.println("Create response code: " + code);
        tresponse.then().log().all();

        Assert.assertTrue(code != 400,
                "Borrower creation SHOULD FAIL for invalid PAN, but API returned 200");

        String referenceId = tresponse.jsonPath().getString("data.id");
        System.out.println("Ref ID: " + referenceId);

        if (referenceId == null) {
            System.out.println("Borrower creation failed as expected. No status API call needed.");
            return;
        }

        Thread.sleep(50000);
        Response statusResponse = Service.createBorrowerStatus(referenceId);
        statusResponse.then().log().all();

        Assert.assertTrue(statusResponse.getStatusCode() >= 400,
                "Expected status API failure for invalid PAN.");
    }
    @Test
    public void MobileCheck() throws IOException,InterruptedException {
        PayloadUtils utils = new PayloadUtils();
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")));
        payload = payload.replace("{{reference_id}}", utils.referenceID);

        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("data").getJSONObject("user_detail")
                .put("phone_no","9092366584");
        payload = jsonObject.toString();

        Response tresponse = Service.createBorrower(payload);

        int code = tresponse.getStatusCode();
        System.out.println("Create response code: " + code);
        tresponse.then().log().all();

        Assert.assertTrue(code != 400,
                "Borrower creation SHOULD FAIL for invalid Mobile Number, but API returned 200");

        String referenceId = tresponse.jsonPath().getString("data.id");
        System.out.println("Ref ID: " + referenceId);

        // If ID is null → API rejected the PAN → Test passes
        if (referenceId == null) {
            System.out.println("Borrower creation failed as expected. No status API call needed.");
            return;
        }

        // else check status
        Thread.sleep(50000);
        Response statusResponse = Service.createBorrowerStatus(referenceId);
        statusResponse.then().log().all();

        Assert.assertTrue(statusResponse.getStatusCode() >= 400,
                "Expected status API failure for invalid PAN.");
    }
    @Test
    public void InvalidProductId() throws IOException,InterruptedException {
        PayloadUtils utils = new PayloadUtils();
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")));
        payload = payload.replace("{{reference_id}}", utils.referenceID);

        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("data").getJSONObject("product_detail")
                .put("product_id","CRE-LAS-PARF");
        payload = jsonObject.toString();

        Response tresponse = Service.createBorrower(payload);

        int code = tresponse.getStatusCode();
        System.out.println("Create response code: " + code);
        tresponse.then().log().all();

        Assert.assertTrue(code != 400,
                "Borrower creation SHOULD FAIL for invalid Product ID, but API returned 200");

        String referenceId = tresponse.jsonPath().getString("data.id");
        System.out.println("Ref ID: " + referenceId);

        if (referenceId == null) {
            System.out.println("Borrower creation failed as expected. No status API call needed.");
            return;
        }

        Thread.sleep(50000);
        Response statusResponse = Service.createBorrowerStatus(referenceId);
        statusResponse.then().log().all();

        Assert.assertTrue(statusResponse.getStatusCode() >= 400,
                "Expected status API failure for invalid PAN.");
    }
    @Test
    public void InvalidEmail() throws IOException,InterruptedException {
        PayloadUtils utils = new PayloadUtils();
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")));
        payload = payload.replace("{{reference_id}}", utils.referenceID);

        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("data").getJSONObject("user_detail")
                .put("user_email","dhivyam@1599@gmail.com");
        payload = jsonObject.toString();

        Response tresponse = Service.createBorrower(payload);

        int code = tresponse.getStatusCode();
        System.out.println("Create response code: " + code);
        tresponse.then().log().all();

        Assert.assertTrue(code != 400,
                "Borrower creation SHOULD FAIL for invalid Email Address, but API returned 200");

        String referenceId = tresponse.jsonPath().getString("data.id");
        System.out.println("Ref ID: " + referenceId);
        if (referenceId == null) {
            System.out.println("Borrower creation failed as expected. No status API call needed.");
            return;
        }
        Thread.sleep(50000);
        Response statusResponse = Service.createBorrowerStatus(referenceId);
        statusResponse.then().log().all();

        Assert.assertTrue(statusResponse.getStatusCode() >= 400,
                "Expected status API failure for invalid Email.");
    }


}
