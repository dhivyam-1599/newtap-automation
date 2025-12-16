package api.tests;
import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;

public class PanValidation {

    @Test
    public void happyPanCase() throws IOException{
        Response response = Service.panvalidation(PayloadUtils.panvalidationjson());
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test
    public void invalidPanCase() throws IOException{
    String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("panNumber", "FXTP89B0");
        payload = jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        boolean success = response.jsonPath().getBoolean("success");
        Assert.assertFalse(success, "Expected success=false but found true");

    }
    @Test
    public void invalidDobCase() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("dateOfBirth", "09/01/1965");
        payload = jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        boolean dobMatch = response.jsonPath().getBoolean("data.dobMatch");
        Assert.assertFalse(dobMatch, "Expected dobMatch=false but found true");
    }

   @Test
    public void happyPanVsAadhar() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("panNumber","COPPS0767D").put("panName","ANANT DATTARAM SAWANT").put("dateOfBirth","03/05/1981");
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
       response.then().log().all();
       Assert.assertEquals(response.getStatusCode(), 200);
       boolean panAadharLinkage = response.jsonPath().getBoolean("data.panAadharLinkage");
       Assert.assertTrue(panAadharLinkage,"Expected panAadharLinkage=true but found false");
   }
    @Test
    public void invaliPanName() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("panName","Omkar Tapkir");
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        boolean success = response.jsonPath().getBoolean("success");
        Assert.assertFalse(success, "Expected success=false but found true");

    }
    @Test
    public void blankPanNumber() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("panNumber"," ");
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 412);
        int statusCode = response.jsonPath().getInt("status.status_code");
        Assert.assertEquals(statusCode, 2008, "Expected status_code to be 2008");

    }
    @Test
    public void blankPanName() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("panName"," ");
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        boolean success = response.jsonPath().getBoolean("success");
        Assert.assertFalse(success, "Expected success=false but found true");
        int statusCode = response.jsonPath().getInt("error_code");
        Assert.assertEquals(statusCode, 2103, "Expected status_code to be 2103");
    }
    @Test
    public void blankDob() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("dateOfBirth"," ");
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test
    public void nullDob() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("dateOfBirth",JSONObject.NULL);
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }
    @Test
    public void invalidDobFormat() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("dateOfBirth","hdjfkkfjf");
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 412);
        int statusCode = response.jsonPath().getInt("status.status_code");
        Assert.assertEquals(statusCode, 2008, "Expected status_code to be 2008");
    }
    @Test
    public void blankProviderPrecedence() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        JSONArray providerArray = new JSONArray();
        providerArray.put(" ");
        jsonObj.put("providerPrecedence", providerArray);
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 412);
        int statusCode = response.jsonPath().getInt("status.status_code");
        Assert.assertEquals(statusCode, 2008, "Expected status_code to be 2008");
    }
    @Test
    public void invalidProviderPrecedence() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject json = new JSONObject(payload);
        JSONArray providerArray = new JSONArray();
        providerArray.put("nsdl");
        json.put("providerPrecedence", providerArray);
        payload=json.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 412);
        int statusCode = response.jsonPath().getInt("status.status_code");
        Assert.assertEquals(statusCode, 2008, "Expected status_code to be 2008");
    }
    @Test
    public void nullProviderPrecedence() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject json = new JSONObject(payload);
        JSONArray providerArray = new JSONArray();
        providerArray.put(JSONObject.NULL);
        json.put("providerPrecedence", providerArray);
        payload=json.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        int statusCode = response.jsonPath().getInt("error_code");
        Assert.assertEquals(statusCode, 2103, "Expected status_code to be 2103");
    }
    @Test
    public void hypervergeHappyCase() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject json = new JSONObject(payload);
        JSONArray providerArray = new JSONArray();
        providerArray.put("HYPERVERGE");
        json.put("providerPrecedence", providerArray);
        payload=json.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);

    }
    @Test
    public void invalidPanVsAadhar() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject jsonObj = new JSONObject(payload);
        jsonObj.put("panNumber","AZOPA2620B").put("panName","ANANT DATTARAM SAWANT").put("dateOfBirth","01/01/1965");
        JSONArray providerArray = new JSONArray();
        providerArray.put("HYPERVERGE");
        jsonObj.put("providerPrecedence", providerArray);
        payload=jsonObj.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        boolean panAadharLinkage = response.jsonPath().getBoolean("data.panAadharLinkage");
        Assert.assertFalse(panAadharLinkage,"Expected panAadharLinkage=false but found true");
    }
    @Test
    public void invalidPanHyperverge() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject json = new JSONObject(payload);
        json.put("panNumber","AMIPT9420P");
        JSONArray providerArray = new JSONArray();
        providerArray.put("HYPERVERGE");
        json.put("providerPrecedence", providerArray);
        payload=json.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
        int statusCode = response.jsonPath().getInt("error_code");
        Assert.assertEquals(statusCode, 2103, "Expected status_code to be 2103");
    }
    @Test
    public void blankPanHyperverge() throws IOException{
        String payload = PayloadUtils.panvalidationjson();
        JSONObject json = new JSONObject(payload);
        json.put("panNumber"," ");
        JSONArray providerArray = new JSONArray();
        providerArray.put("HYPERVERGE");
        json.put("providerPrecedence", providerArray);
        payload=json.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 412);
        int statusCode = response.jsonPath().getInt("status.status_code");
        Assert.assertEquals(statusCode, 2008, "Expected status_code to be 2008");
    }
    @Test
    public void multipleProviders() throws IOException {
        String payload = PayloadUtils.panvalidationjson();
        JSONObject json = new JSONObject(payload);
        JSONArray providerArray = new JSONArray();
        providerArray.put("HYPERVERGE");
        providerArray.put("NSDL");
        json.put("providerPrecedence", providerArray);
        payload = json.toString();
        Response response = Service.panvalidation(payload);
        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200);
    }






}
