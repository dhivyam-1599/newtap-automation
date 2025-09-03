package api.newtap_api;

import static api.newtap_api.BaseUrl.base_url_onboarding;
import static api.newtap_api.BaseUrl.cash_base_url_onboarding;

public class Endpoints {


    public static String create_borrower_url = base_url_onboarding+"/createBorrower";
    public static String create_borrower_status_url = create_borrower_url+"/status";
    public static String update_borrower_url = base_url_onboarding+"/updateBorrower";
    public static String cash_create_borrower_url=cash_base_url_onboarding+"/createBorrower";
    public static String cash_vcip_url = cash_base_url_onboarding +"/validate/VCIPStatus";

}
