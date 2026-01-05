package api.helpers;

import api.newtap_api.Service;
import api.utilities.PayloadUtils;
import io.restassured.response.Response;
import org.testng.Assert;

import java.io.IOException;

public class UnderwriterHelper {
        public static Response callUnderwriter(String tenant, String payload) {
            Response response = Service.underwriter(payload, tenant);
            response.then().log().all();
            Assert.assertEquals(response.getStatusCode(), 200);
            return response;

    }
}