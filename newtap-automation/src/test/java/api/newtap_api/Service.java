package api.newtap_api;

import api.utilities.HeaderUtil;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;


public class Service {

    public static Response createborrower(String payload){
        return HeaderUtil.getHeaders("LAS")
                .body(payload)
                .when()
                .log().all()
                .post(Endpoints.create_borrower_url)
                .then()
                .extract().response();
    }
    public static Response createborrowerstatus(String ref_id){
        return HeaderUtil.getHeaders("LAS")
                .queryParams("reference_id",ref_id)
                .when()
                .log().all()
                .get(Endpoints.create_borrower_status_url)
                .then()
                .extract().response();
    }
    public static Response updateborrower(String payload){
        return HeaderUtil.getHeaders("LAS")
                .body(payload)
                .when()
                .log().all()
                .post(Endpoints.update_borrower_url)
                .then()
                .extract().response();
    }
    public static Response getpresignedurls(String objectUrls){

        return (Response) given()
                .contentType("application/json")
                .header("X-APP-TOKEN","e713e3f1-6016-43ca-8119-3d0e971265da")
                .queryParams("object_path",objectUrls)
                .queryParams("ttl","300")
                .queryParams("s3_bucket_name","janus-parfait-secret-newtapuat")
                .when()
                .get(BaseUrl.base_url_thirdparty)
                .then()
                .extract()
                .response();

   }
   public static Response cashonboarding(String payload) {
       return HeaderUtil.getHeaders("CASH")
               .body(payload)
               .when()
               .log().all()
               .post(Endpoints.cash_create_borrower_url)
               .then()
               .extract().response();
   }

}
