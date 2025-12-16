package api.newtap_api;

import static api.newtap_api.BaseUrl.*;

public class EndPoints {


    public static String CREATE_BORROWER_URL = BASE_URL_ONBOARDING+"/createBorrower";
    public static String CREATE_BORROWER_STATUS_URL = CREATE_BORROWER_URL+"/status";
    public static String UPDATE_BORROWER_URL = BASE_URL_ONBOARDING+"/updateBorrower";
    public static String CASH_CREATE_BORROWER_URL=CASH_BASE_URL_ONBOARDING+"/createBorrower";
    public static String CASH_CREATE_BORROWER_STATUS_URL =CASH_CREATE_BORROWER_URL+"/status";
    public static String NEWTAP_CREATE_BORROWER_STATUS_URL =CASH_CREATE_BORROWER_URL+"/status";
    public static String VALIDATE_VCIP_URL = CASH_BASE_URL_ONBOARDING+"/validate/VCIPStatus";
    public static String CREDIT_UPDATE_URL = BUREAU_CREDIT_UPDATE_URL+"/update";
    public static String KYC_SEARCH_URL = CYNTRA_URL+"search";
    public static String PAN_VALIDATION = BASE_THIRDPARTY_URL+"/pan/validate";

}
