package api.tests;

import org.json.JSONArray;
import org.json.JSONObject;
    public class GenerateJson {
        public static void main(String[] args) {
            JSONObject root = new JSONObject();
            JSONArray requests = new JSONArray();

            JSONObject request = new JSONObject();
            request.put("entityIdHash", "2d183a08ebc488ef95f7f7b7f75e67f137e7bfd3c23141ecc8700ef1912d8f");
            request.put("customerId", "5af1820c-7b05-4a8b-a205-77c955c90e6d");
            request.put("aadhaarPdfS3Url", "https://janus-parfait-secret-newtapuat.s3.ap-south-1.amazonaws.com/kyc_details/digilocker/download_results/2025/11/05/05/5af1820c-7b05-4a8b-a205-77c955c90e6d.json");
            request.put("digilockerJsonS3Url", "https://janus-parfait-secret-newtapuat.s3.ap-south-1.amazonaws.com/kyc_details/digilocker/download_results/2025/11/05/05/5af1820c-7b05-4a8b-a205-77c955c90e6d.json");
            request.put("tenant", "NEWTAP");
            request.put("lob", "CASH");
            for (int i = 0; i < 1000; i++) {
                requests.put(request);
            }

            root.put("requests", requests);

            System.out.println(root.toString(2));
        }
    }


