package api.tests;

import api.helpers.OnboardingHelper;
import api.utilities.PayloadUtils;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.io.IOException;

public class CashUnderwriterSanityCases {
    @Test
    public void happyYblPremiumPolicy() throws IOException {
        System.setProperty("cash-Tenant", "NEWTAP_YBL");
        OnboardingHelper.createColendingCashNonVcip();
    }
    @Test
    public void happyYblPremiumPlusPolicy() throws IOException {

        System.setProperty("cash-Tenant", "NEWTAP_YBL");
        System.setProperty("policy_name", "ybl_clm_premium_plus_v1");
        System.setProperty("policy_identifier", "ybl_clm_premium_plus_v1");

        OnboardingHelper.createColendingCashNonVcip();
    }
    @Test
    public void happyYblSuperPremiumPolicy() throws IOException {

        System.setProperty("cash-Tenant", "NEWTAP_YBL");
        System.setProperty("policy_name", "ybl_clm_super_premium_v1");
        System.setProperty("policy_identifier", "ybl_clm_super_premium_v1");
        OnboardingHelper.createColendingCashNonVcip();
    }
    @Test
    public void nullGrmDecileYbl() throws IOException {

        System.setProperty("cash-Tenant", "NEWTAP_YBL");

        System.setProperty(
                "payload.override",
                "{ \"risk_detail\": { \"user_data\": { \"grm_decile\": null } } }"
        );

        OnboardingHelper.createColendingCashNonVcip();
    }
    @Test
    public void nullStv3Ybl() throws IOException {
        System.setProperty("cash-Tenant", "NEWTAP_YBL");
        System.setProperty(
                "payload.override",
                "{ \"risk_detail\": { \"user_data\": { \"stv3\": null } } }"
        );
        OnboardingHelper.createColendingCashNonVcip();
    }
    @Test
    public void invalidGrmDecileYbl() throws IOException {
        System.setProperty("cash-Tenant", "NEWTAP_YBL");
        System.setProperty(
                "payload.override",
                "{ \"risk_detail\": { \"user_data\": { \"grm_decile\": \"INVALID_VALUE\" } } }"
        );
        OnboardingHelper.createColendingCashNonVcip();
    }





}
