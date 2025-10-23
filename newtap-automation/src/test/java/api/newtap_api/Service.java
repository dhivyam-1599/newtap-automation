package api.newtap_api;
import api.utilities.HeaderUtil;
import io.restassured.response.Response;

import static api.newtap_api.EndPoints.CREDIT_UPDATE_URL;
import static api.utilities.HeaderUtil.getEndpoint;
import static io.restassured.RestAssured.given;


public class Service {

    public static Response createBorrower(String payload){
        return HeaderUtil.getHeaders("LAS")
                .body(payload)
                .when()
                .log().all()
                .post(EndPoints.CREATE_BORROWER_URL)
                .then()
                .extract().response();
    }
    public static Response createBorrowerStatus(String ref_id){
        return HeaderUtil.getHeaders("LAS")
                .queryParams("reference_id",ref_id)
                .when()
                .log().all()
                .get(EndPoints.CREATE_BORROWER_STATUS_URL)
                .then()
                .extract().response();
    }
    public static Response updateBorrower(String payload){
        return HeaderUtil.getHeaders("LAS")
                .body(payload)
                .when()
                .log().all()
                .post(EndPoints.UPDATE_BORROWER_URL)
                .then()
                .extract().response();
    }
    public static Response getPresignedUrls(String objectUrls){

        return (Response) given()
                .contentType("application/json")
                .header("X-APP-TOKEN","e713e3f1-6016-43ca-8119-3d0e971265da")
                .queryParams("object_path",objectUrls)
                .queryParams("ttl","300")
                .queryParams("s3_bucket_name","janus-parfait-secret-newtapuat")
                .when()
                .get(BaseUrl.BASE_URL_THIRDPARTY)
                .then()
                .extract()
                .response();

    }
    public static Response cashOnboarding(String payload) {
        return HeaderUtil.getHeaders("CASH")
                .body(payload)
                .when()
                .log().all()
                .post(EndPoints.CASH_CREATE_BORROWER_STATUS_URL)
                .then()
                .extract().response();
    }
    public static Response newtapCashOnboarding(String payload) {
        return HeaderUtil.getHeaders("NEWTAP100%")
                .body(payload)
                .when()
                .log().all()
                .post(EndPoints.CASH_CREATE_BORROWER_STATUS_URL)
                .then()
                .extract().response();
    }
    public static Response cashCreateBorrowerStatus(String serviceType, String referenceId) {
        return HeaderUtil.getHeaders(serviceType)
                .queryParam("reference_id", referenceId)
                .when()
                .log().all()
                .get(getEndpoint(serviceType))   // endpoint based on service
                .then()
                .extract().response();
    }
    public static Response validateVcip(String userID,String payload) {
        return (Response) given()
                .contentType("application/json")
                .header("X-APP-TOKEN", "e713e3f1-6016-43ca-8119-3d0e971265da")
                .header("X-Request-Id", userID)
                .header("X-User-Id", userID)
                .header("X-APP-TOKEN", "heracles")
                .body(payload)
                .when()
                .log().all()
                .post(EndPoints.VALIDATE_VCIP_URL)
                .then()
                .extract()
                .response();
    }
        public static Response creditReportUpdate(String payload) {
            return (Response) given()
                    .contentType("application/json")
                    .body(payload)
                    .when()
                    .log().all()
                    .post(EndPoints.CREDIT_UPDATE_URL)
                    .then()
                    .extract()
                    .response();




    }}
