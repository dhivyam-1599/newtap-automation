package api.helpers;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import api.utilities.database.DbQuery;
import io.restassured.response.Response;
import org.testng.Assert;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import static api.utilities.PayloadUtils.las_appform;
import static api.utilities.PayloadUtils.las_crn;
import static org.awaitility.Awaitility.await;
public class OnboardingHelper {

    public static Response createBorrower() throws IOException, InterruptedException {
        String referenceID = PayloadUtils.generateReferenceId();

        String createJson = new String(Files.readAllBytes(
                Paths.get("src/test/resources/payloads/createborrower.json")
        ));
        createJson = createJson.replace("{{reference_id}}", referenceID);

        Response response = Service.createBorrower(createJson);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);

        Thread.sleep(15000);

        return response;
    }
    public static Response creditReportUpdate() throws IOException, InterruptedException {
        DbQuery dbQuery = new DbQuery();
        String requestID = dbQuery.getRequestId(las_crn);

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


    // ---- COMMON WAIT METHOD ----
    public static Response waitForStatus(String serviceType, String referenceId, String expectedStatus, int maxMinutes) {
        await()
                .atMost(maxMinutes, TimeUnit.MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .until(() -> {
                    Response poll = Service.cashCreateBorrowerStatus(serviceType, referenceId);
                    String status = poll.jsonPath().getString("data.status");
                    System.out.println("Current status: " + status);
                    return expectedStatus.equals(status);
                });

        Response finalStatus = Service.cashCreateBorrowerStatus(serviceType, referenceId);
        Assert.assertEquals(finalStatus.getStatusCode(), 200);
        return finalStatus;
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


    // ---- CASH NON-VCIP BORROWER ----
    public static String createColendingCashNonVcip() throws IOException {
        Response resp = Service.cashOnboarding(PayloadUtils.buildCashMigrationNonVcipJson());
        Assert.assertEquals(resp.getStatusCode(), 200);
        String refId = resp.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + refId);
        waitForStatus("CASH", refId, "APPROVED", 2);
        return refId;
    }

    // ---- NEWTAP NON-VCIP ----
    public static String createNewtapCashNonVcip() throws IOException {
        Response resp = Service.newtapCashOnboarding(PayloadUtils.buildNewtapCashMigrationNonVcpJson());
        Assert.assertEquals(resp.getStatusCode(), 200);
        String refId = resp.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : " + refId);
        waitForStatus("NEWTAP100%", refId, "APPROVED", 2);
        return refId;
    }

    // ---- CASH VCIP ----
    public static void runColendingCashVcip() throws IOException {
        String payload = PayloadUtils.buildCashMigrationJson()
                .replace("\"vcip_enabled\": false", "\"vcip_enabled\": true");

        Response resp = Service.cashOnboarding(payload);
        Assert.assertEquals(resp.getStatusCode(), 200);

        String refId = resp.jsonPath().getString("data.id");
        Response finalStatus = waitForStatus("CASH", refId, "IN_PROGRESS", 2);

        String userId = finalStatus.jsonPath().getString("data.user_id");
        String formId = finalStatus.jsonPath().getString("data.form_id");

        String vcipJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/vcip.json")))
                .replace("{{app_form_id}}", formId);

        Response vcip = Service.validateVcip(userId, vcipJson);
        Assert.assertEquals(vcip.getStatusCode(), 200);
        Assert.assertTrue(vcip.jsonPath().getBoolean("success"));
    }

    // ---- NEWTAP VCIP ----
    public static void runNewtapCashVcip() throws IOException {
        String payload = PayloadUtils.buildNewtapCashMigrationJson()
                .replace("\"vcip_enabled\": false", "\"vcip_enabled\": true");

        Response resp = Service.newtapCashOnboarding(payload);
        Assert.assertEquals(resp.getStatusCode(), 200);

        String refId = resp.jsonPath().getString("data.id");
        Response finalStatus = waitForStatus("NEWTAP100%", refId, "IN_PROGRESS", 2);

        String userId = finalStatus.jsonPath().getString("data.user_id");
        String formId = finalStatus.jsonPath().getString("data.form_id");

        performVcip(userId, formId);
    }

    // ---- REUSABLE VCIP HELPER ----
    public static void performVcip(String userId, String formId) throws IOException {
        String vcipJson = new String(
                Files.readAllBytes(Paths.get("src/test/resources/payloads/vcip.json"))
        ).replace("{{app_form_id}}", formId);

        Response vcipResponse = Service.validateVcip(userId, vcipJson);
        vcipResponse.then().log().all();
        Assert.assertEquals(vcipResponse.getStatusCode(), 200);
        Assert.assertTrue(vcipResponse.jsonPath().getBoolean("success"));
    }


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
}
