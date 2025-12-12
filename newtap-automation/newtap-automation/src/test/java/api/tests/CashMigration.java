package api.tests;
import api.helpers.OnboardingHelper;
import org.json.JSONObject;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import static api.helpers.OnboardingHelper.waitForStatus;


public class CashMigration {

    @Test
    public void testCashNonVcip() throws Exception {
        OnboardingHelper.createColendingCashNonVcip();
    }

    @Test
    public void testNewtapNonVcip() throws Exception {
        OnboardingHelper.createNewtapCashNonVcip();
    }

    @Test
    public void testCashVcip() throws Exception {
        OnboardingHelper.runColendingCashVcip();
    }

    @Test
    public void NewtapCashVcip() throws Exception {
        String payload = PayloadUtils.buildNewtapCashMigrationJson()
                .replace("\"vcip_enabled\": false", "\"vcip_enabled\": true");

        Response response = Service.newtapCashOnboarding(payload);
        String referenceId = response.jsonPath().getString("data.id");

        Response finalStatus = waitForStatus("NEWTAP100%", referenceId, "IN_PROGRESS", 2);

        OnboardingHelper.performVcip(
                finalStatus.jsonPath().getString("data.user_id"),
                finalStatus.jsonPath().getString("data.form_id")
        );
    }

    @Test
    public void CashInvalidPan() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.getJSONObject("pan_detail")
                .put("pan_number", "RTHK788JJJHH");
        payload = jsonObj.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(35000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
    }
    @Test

    public void CashDobCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.getJSONObject("pan_detail")
                .put("dob", "2008-01-01");
        payload = jsonObj.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for CashDobCheck, but got: " + statusCode);
    }
    @Test
    public void PanVsBankCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("pan_detail").put("pan_name","Aruna");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for PanVsBankCheck, but got: " + statusCode);
    }
    @Test
    public void PanVsKycCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("kyc_detail").getJSONObject("data")
                .put("name", "Saranya");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for PanVsKycCheck, but got: " + statusCode);
    }
    @Test
    public void AccountTypeValidation() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("kyc_detail").getJSONObject("data")
                .put("account_type", "XYZ");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for invalid Account type, but got: " + statusCode);
    }
    @Test
    public void CkycS3UrlCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("kyc_detail").getJSONObject("data")
                .put("os_ocr_url",JSONObject.NULL);
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for null CkycS3Url, but got: " + statusCode);
    }
    @Test
    public void GeoLocationCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("geo_location_verification_detail").getJSONObject("data")
                .put("cloudfront-viewer-country","UK");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for Invalid Geolocation, but got: " + statusCode);
    }
    @Test

    public void PacCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("kyc_detail").getJSONObject("data")
                .put("pac_reference_id",JSONObject.NULL);
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
    }
    @Test
    public void FaceMatchCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationNonVcipJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("liveliness_detail").getJSONObject("data").getJSONObject("face_match")
                .put("score","40");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for Invalid Face match score, but got: " + statusCode);
    }
    @Test
    public void LivelinessCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationNonVcipJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("liveliness_detail").getJSONObject("data").getJSONObject("liveliness")
                .put("score","30");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for Invalid liveliness score , but got: " + statusCode);
    }
    @Test
    public void NameCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("kyc_detail").getJSONObject("data")
                .put("name","Div");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for invalid Name, but got: " + statusCode);
    }
    @Test
    public void PincodeCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("kyc_detail").getJSONObject("data").getJSONObject("segregated_address")
                .put("pincode","180019");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for invalid Pincode, but got: " + statusCode);
    }
    @Test
    public void AmlCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("pan_detail")
                .put("pan_number","MPSPS4100L").put("pan_name","Dawood Ibrahim Kaskar");
        payload= jsonObject.toString();
        Response response = Service.cashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for AML Screening, but got: " + statusCode);
    }
    @Test
    public void UnderwritingCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildNewtapCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("risk_detail").getJSONObject("offer_data")
                .put("limit",100000000);
        payload= jsonObject.toString();
        System.out.println(payload);
        Response response = Service.newtapCashOnboarding(payload);
        Assert.assertEquals(response.getStatusCode(),200,"Borrower creation should still succeed");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        Thread.sleep(50000);
        Response statusResponse = Service.cashCreateBorrowerStatus("CASH", referenceId);
        statusResponse.then().log().all();
        int statusCode = statusResponse.getStatusCode();
        Assert.assertTrue(statusCode == 200 || statusCode == 201,
                "Expected 400/500 for invalid Offer data, but got: " + statusCode);
    }


}





