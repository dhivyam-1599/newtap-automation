package api.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static api.tests.onboarding_las.GetPresignedUrl;


public class PayloadUtils {
    public static String referenceID = generateReferenceId();
    public static String las_appform;
    public static String las_crn;
    public static String createJson;
    public static String updateJson;
    public static String cashcreateJson;


    public static Map<String, String> JsonHelper() throws IOException {
        if (las_appform == null || las_crn == null) {
            throw new IllegalStateException("las_appform or las_crn is null. Make sure to call createborrower before JsonHelper()");
        }

        createJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/createborrower.json")));
        updateJson = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/updateborrower.json")));

//         Replace base references
        createJson = createJson.replace("{{reference_id}}", referenceID);
        updateJson = updateJson
                .replace("{{external_reference_id}}", las_appform)
                .replace("{{crn}}", las_crn)
                .replace("{{reference_id}}", referenceID); // This is also required in updateBorrower body
        // Inject pre-signed URLs
        Map<String, String> presignedPaths = PreSignedHelper();

        updateJson = updateJson
                .replace("{{image_url}}",presignedPaths.get("image_url"))
                .replace("{{selfie_image}}",presignedPaths.get("selfie_image"))
                .replace("{{search_result_location}}", presignedPaths.get("search_result_location"))
                .replace("{{download_result_location}}", presignedPaths.get("download_result_location"));


        Map<String, String> result = new HashMap<>();
        result.put("createJson", createJson);
        result.put("updateJson", updateJson);
        return result;
    }
    public static String buildCashMigrationJson() throws IOException {
        String json = new String(Files.readAllBytes(Paths.get("src/test/resources/payloads/cashmigrationcolending.json")));
        Map<String, String> presigned = PreSignedHelper();

        return json
                .replace("{{client_ref_id}}", referenceID)
                .replace("{{image_url}}", presigned.get("image_url"))
                .replace("{{selfie_image}}", presigned.get("selfie_image"))
                .replace("{{search_result_location}}", presigned.get("search_result_location"))
                .replace("{{download_result_location}}", presigned.get("download_result_location"));
    }


    public static String generateReferenceId() {
        return "CRE_LAS_UAT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    public static Map<String, String> PreSignedHelper() throws IOException {
        String imageObjectPath = "kyc_details/ckyc/images/2025/07/31/04/63e7b3d4-6203-451c-a09e-fb45be1b7f5b.jpeg";
        String selfieObjectPath = "kyc_details/ckyc/images/2025/07/31/04/63e7b3d4-6203-451c-a09e-fb45be1b7f5b.jpeg";
        String searchObjectPath = "kyc_details/ckyc/search_results/2025/07/31/04/63e7b3d4-6203-451c-a09e-fb45be1b7f5b.json";
        String downloadObjectPath = "kyc_details/ckyc/download_results/2025/08/19/12/29e2a31c-f157-471d-a586-72a88f6f2c04.json";

        String image_url = GetPresignedUrl(imageObjectPath);
        String selfie_image = GetPresignedUrl(selfieObjectPath);
        String search_result_location = GetPresignedUrl(searchObjectPath);
        String download_result_location = GetPresignedUrl(downloadObjectPath);

        System.out.println("image_url: " + image_url);
        System.out.println("selfie_image: " + selfie_image);
        System.out.println("search_result_location: " + search_result_location);
        System.out.println("download_result_location: " + download_result_location);

        if (image_url == null || selfie_image == null || search_result_location == null || download_result_location == null) {
            throw new IllegalStateException("One or more pre-signed URLs are null. Check S3 object paths or credentials.");
        }

        Map<String, String> objectUrls = new HashMap<>();
        objectUrls.put("image_url", image_url);
        objectUrls.put("selfie_image", selfie_image);
        objectUrls.put("search_result_location", search_result_location);
        objectUrls.put("download_result_location", download_result_location);

        return objectUrls;
    }

}

