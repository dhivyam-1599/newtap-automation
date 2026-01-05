package api.tests;

import api.helpers.UnderwriterHelper;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import api.utilities.TestDataProviders;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;

public class Underwriter {


    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void happyUnderwriter(String tenant) throws IOException {
        String payload = PayloadUtils.underwriterJson(tenant);
        Response response = Service.underwriter(payload, tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void nullGrmDecile(String tenant) throws IOException{
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("grm_decile",JSONObject.NULL);
         String updatedpayload = json.toString();
        Response response = Service.underwriter(updatedpayload,tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);

    }
    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void nullStv3(String tenant) throws IOException{
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.getJSONObject("user_data").put("stv3",JSONObject.NULL);
        String updatedpayload = json.toString();
        Response response = Service.underwriter(updatedpayload,tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void nullGrmAndStv3(String tenant) throws IOException {
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        if (json.has("grm_decile")) {
            json.put("grm_decile", JSONObject.NULL);
        }
        if (json.has("user_data")) {
            JSONObject userData = json.getJSONObject("user_data");
            if (userData.has("grm_decile")) {
                userData.put("grm_decile", JSONObject.NULL);
            }
            if (userData.has("stv3")) {
                userData.put("stv3", JSONObject.NULL);
            }
        }
        String updatedPayload = json.toString();
        Response response = Service.underwriter(updatedPayload, tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void missingGrmDecile(String tenant) throws IOException {
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        JSONObject userData = json.getJSONObject("user_data");
        userData.remove("grm_decile");
        String updatedPayload = json.toString();
        Response response = Service.underwriter(updatedPayload, tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void missingStv3(String tenant) throws IOException {
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        JSONObject userData = json.getJSONObject("user_data");
        userData.remove("stv3");
        String updatedPayload = json.toString();
        Response response = Service.underwriter(updatedPayload, tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void invalidDataTypeGrm(String tenant) throws IOException {
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.getJSONObject("user_data").put("grm_decile","invalid data type");
        String updatedPayload = json.toString();
        Response response = Service.underwriter(updatedPayload, tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 400);
    }
    @Test(dataProvider = "tenantProvider", dataProviderClass = TestDataProviders.class)
    public void invalidDataTypeStv3(String tenant) throws IOException {
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.getJSONObject("user_data").put("stv3","invalid data type");
        String updatedPayload = json.toString();
        Response response = Service.underwriter(updatedPayload, tenant);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 400);
    }

    @Test
    public void underwriterYblPremium() throws Exception {
        String tenant = "NEWTAP_YBL";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","ybl_clm_premium_plus_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void underwriterYblSuperPremium() throws Exception {
        String tenant = "NEWTAP_YBL";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","ybl_clm_super_premium_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void grmYblRangeFailure() throws Exception {
        String tenant = "NEWTAP_YBL";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.getJSONObject("user_data").put("grm_decile",10);
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void stv3YblRangeFailure() throws Exception {
        String tenant = "NEWTAP_YBL";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.getJSONObject("user_data").put("stv3",90);
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void underwriterLtfSuperPremium() throws Exception {
        String tenant = "NEWTAP_LTF";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","ltf_clm_super_premium_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void underwriterLtfPremium() throws Exception {
        String tenant = "NEWTAP_LTF";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","ltf_clm_premium_plus_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void grmLtfRangeFailure() throws Exception {
        String tenant = "NEWTAP_LTF";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.getJSONObject("user_data").put("grm_decile",20);
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void stv3LtfRangeFailure() throws Exception {
        String tenant = "NEWTAP_LTF";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.getJSONObject("user_data").put("stv3",90);
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void underwriterParfaitBau() throws Exception {
        String tenant = "PARFAIT";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","pl_bau_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    //To check
    @Test
    public void underwriterParfaitBureauIncome() throws Exception {
        String tenant = "PARFAIT";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","pl_bureau_income_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void underwriterParfaitBureauSurrogate() throws Exception {
        String tenant = "PARFAIT";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","pl_bureau_surrogate_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }
    @Test
    public void underwriterParfaitHighRoi() throws Exception {
        String tenant = "PARFAIT";
        String payload = PayloadUtils.underwriterJson(tenant);
        JSONObject json = new JSONObject(payload);
        json.put("policy_name","pl_high_roi_v1");
        String yblpayload = json.toString();
        UnderwriterHelper.callUnderwriter(tenant, yblpayload);
    }



}
