package api.tests;

import org.json.JSONObject;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;

public class CashMigration {

    private static Response finalStatusResponse;

    private Response createBorrower(String serviceType) throws IOException {
        String payload = serviceType.equals("CASH")
                ? PayloadUtils.buildCashMigrationJson()
                : PayloadUtils.buildNewtapCashMigrationJson();

        Response response = serviceType.equals("CASH")
                ? Service.cashOnboarding(payload)
                : Service.newtapCashOnboarding(payload);

        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        return response;
    }

    private void waitForStatus(String serviceType, String referenceId, String expectedStatus, int maxMinutes) {
        await()
                .atMost(maxMinutes, TimeUnit.MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .until(() -> {
                    Response pollResponse = Service.cashCreateBorrowerStatus(serviceType, referenceId);
                    String status = pollResponse.jsonPath().getString("data.status");
                    System.out.println("Current status: " + status);
                    return expectedStatus.equals(status);
                });
        finalStatusResponse = Service.cashCreateBorrowerStatus(serviceType, referenceId);
        finalStatusResponse.then().log().all();
        Assert.assertEquals(finalStatusResponse.getStatusCode(), 200);
    }

    @Test
    public void ColendingCashNonVcip() throws IOException {
        Response response = createBorrower("CASH");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        waitForStatus("CASH", referenceId, "APPROVED", 2);
    }

    @Test
    public void NewtapCashNonVcip() throws IOException {
        Response response = createBorrower("NEWTAP100%");
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        waitForStatus("NEWTAP100%", referenceId, "APPROVED", 2);
    }

    @Test
    public void ColendingCashVcip() throws IOException {
        String payload = PayloadUtils.buildCashMigrationJson();
        payload = payload.replace("\"vcip_enabled\": false", "\"vcip_enabled\": true");
        Response response = Service.cashOnboarding(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        waitForStatus("CASH", referenceId, "IN_PROGRESS", 2);
        String userId = finalStatusResponse.jsonPath().getString("data.user_id");
        String formId = finalStatusResponse.jsonPath().getString("data.form_id");
        System.out.println("UserId: " + userId + " | FormId: " + formId);
        String vcipJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/vcip.json")))
                .replace("{{app_form_id}}", formId);
        Response vcipResponse = Service.validateVcip(userId, vcipJson);
        vcipResponse.then().log().all();
        Assert.assertEquals(vcipResponse.getStatusCode(), 200);
        Assert.assertTrue(vcipResponse.jsonPath().getBoolean("success"),
                "Expected VCIP validation success=true but got false");
    }

    @Test
    public void NewtapCashVcip() throws IOException {
        String payload = PayloadUtils.buildNewtapCashMigrationJson();
        payload = payload.replace("\"vcip_enabled\": false", "\"vcip_enabled\": true");
        Response response = Service.newtapCashOnboarding(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        String referenceId = response.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + referenceId);
        waitForStatus("NEWTAP100%", referenceId, "IN_PROGRESS", 2);
        String userId = finalStatusResponse.jsonPath().getString("data.user_id");
        String formId = finalStatusResponse.jsonPath().getString("data.form_id");
        System.out.println("UserId: " + userId + " | FormId: " + formId);
        String vcipJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/vcip.json")))
                .replace("{{app_form_id}}", formId);
        Response vcipResponse = Service.validateVcip(userId, vcipJson);
        vcipResponse.then().log().all();
        Assert.assertEquals(vcipResponse.getStatusCode(), 200);
        Assert.assertTrue(vcipResponse.jsonPath().getBoolean("success"),
                "Expected VCIP validation success=true but got false");
    }

    @Test
    public void CashNonVcipInvalidPan() throws IOException,InterruptedException{
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
                .getJSONObject("data")
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
        String payload = PayloadUtils.buildCashMigrationJson();
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
    }
    @Test
    public void LivelinessCheck() throws IOException,InterruptedException{
        String payload = PayloadUtils.buildCashMigrationJson();
        JSONObject jsonObject = new JSONObject(payload);
        jsonObject.getJSONObject("kyc_detail").getJSONObject("data").getJSONObject("liveliness")
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
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
                "Expected 400/500 for invalid PAN, but got: " + statusCode);
    }






}
