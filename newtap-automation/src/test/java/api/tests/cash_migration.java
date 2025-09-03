package api.tests;

import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;

public class cash_migration {

    @Test
    public void Colendingonboarding()throws IOException {
        String cashJson = PayloadUtils.buildCashMigrationJson();
        Response cashcreateresponse = Service.cashonboarding(cashJson);
        cashcreateresponse.then().log().all();
        Assert.assertEquals(cashcreateresponse.getStatusCode(), 200);
    }

    @Test
    public void Newtaponboarding()throws IOException{
     String parfaitJson = PayloadUtils.buildNewtapCashMigrationJson();
     Response newtapcreateresponse = Service.newtapcashonboarding(parfaitJson);
     newtapcreateresponse.then().log().all();
     Assert.assertEquals(newtapcreateresponse.getStatusCode(),200);
    }

}
