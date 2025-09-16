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

public class cash_migration {

    private static Response finalStatusResponse;

    public Response ColendingCreateHelper() throws IOException {
        String cashJson = PayloadUtils.buildCashMigrationJson();
        Response cashcreateresponse = Service.cashonboarding(cashJson);
        cashcreateresponse.then().log().all();
        Assert.assertEquals(cashcreateresponse.getStatusCode(), 200);
        return cashcreateresponse;
    }
    public Response ParfaitCreateHelper()throws IOException{
        String parfaitJson = PayloadUtils.buildNewtapCashMigrationJson();
        Response newtapcreateresponse = Service.newtapcashonboarding(parfaitJson);
        newtapcreateresponse.then().log().all();
        Assert.assertEquals(newtapcreateresponse.getStatusCode(),200);
        return newtapcreateresponse;
    }

    @Test
    public void ColendingCashCreateBorrower() throws IOException {
        Response cashBorrowerResponse = ColendingCreateHelper();
        String reference_id = cashBorrowerResponse.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : "+reference_id);
        await()
                .atMost(2, TimeUnit.MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .until(() -> {
                    Response pollResponse = Service.cashcreateborrowerstatus("CASH",reference_id);
                    String status = pollResponse.jsonPath().getString("data.status");
                    System.out.println("Current status: " + status);
                    return "APPROVED".equals(status);
                });
        finalStatusResponse = Service.cashcreateborrowerstatus("CASH",reference_id);
        finalStatusResponse.then().log().all();
        Assert.assertEquals(finalStatusResponse.getStatusCode(), 200);
    }

    @Test
    public void NewtapCashCreateBorrower() throws IOException {
        Response newtapBorrowerResponse = ParfaitCreateHelper();
        String reference_id = newtapBorrowerResponse.jsonPath().getString("data.id");
        System.out.println("Workflow_ID : "+reference_id);
        await()
                .atMost(2, TimeUnit.MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .until(() -> {
                    Response pollResponse = Service.cashcreateborrowerstatus("NEWTAP100%",reference_id);
                    String status = pollResponse.jsonPath().getString("data.status");
                    System.out.println("Current status: " + status);
                    return "APPROVED".equals(status);
                });
        finalStatusResponse = Service.cashcreateborrowerstatus("NEWTAP100%",reference_id);
        finalStatusResponse.then().log().all();
        Assert.assertEquals(finalStatusResponse.getStatusCode(), 200);
    }

    @Test(dependsOnMethods = "ColendingCashCreateBorrower")
    public void ColendingVCIP() throws IOException {
        String userId = finalStatusResponse.jsonPath().getString("data.user_id");
        String formId = finalStatusResponse.jsonPath().getString("data.form_id");
        System.out.println("UserId: " + userId + " | FormId: " + formId);

        String vcipjson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/vcip.json")));
        vcipjson = vcipjson.replace("{{app_form_id}}", formId);
        Response vcipResponse = Service.validatevcip(userId, vcipjson);
        vcipResponse.then().log().all();
        Assert.assertEquals(vcipResponse.getStatusCode(), 200);
        Assert.assertTrue(vcipResponse.jsonPath().getBoolean("success"),
                "Expected VCIP validation success=true but got false");
    }


}


