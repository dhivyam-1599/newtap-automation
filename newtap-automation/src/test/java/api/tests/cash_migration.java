package api.tests;

import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;

public class cash_migration {

    @Test
    public void Colendingonboarding()throws IOException,InterruptedException {
        String cashJson = PayloadUtils.buildCashMigrationJson();
        Response cashcreateresponse = Service.cashonboarding(cashJson);
        cashcreateresponse.then().log().all();
        Assert.assertEquals(cashcreateresponse.getStatusCode(), 200);
    }


}
