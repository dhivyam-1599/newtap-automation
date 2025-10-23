package api.tests;
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

}
