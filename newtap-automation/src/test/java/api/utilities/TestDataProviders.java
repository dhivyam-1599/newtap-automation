package api.utilities;

import org.testng.annotations.DataProvider;

public class TestDataProviders {
    @DataProvider(name = "tenantProvider")
    public Object[][] tenantProvider() {
        return new Object[][]{
                {"PARFAIT"},
                {"NEWTAP_YBL"},
                {"NEWTAP_LTF"}
        };
    }


}
