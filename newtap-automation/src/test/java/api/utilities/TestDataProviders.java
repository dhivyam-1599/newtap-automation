package api.utilities;

import org.testng.annotations.DataProvider;

public class TestDataProviders {
    @DataProvider(name = "invalidPANDetails")
    public Object[][] invalidPANDetails() {
        return new Object[][]{
                {"ABCDE12345", "ARUNA M", "Invalid Pan Details"}
        };
    }

    @DataProvider(name = "invalidPANAadhar")
    public Object[][] invalidPANAadhar() {
        return new Object[][]{
                {"mwSwGfJkKnlyjkLnMnvjtQ==","DHIVYA M","Invalid PAN Aadhar Linkage"}
        };
    }
    @DataProvider(name = "invalidUserdetails")
    public Object[][] invalidUserdetails() {
        return new Object[][]{
                {"bac0034f-4141-47cf-aea2-4367f3857","9092366584","DHIVYA M","dhivyam1599@gmail.com","Invalid User Details"}
        };
    }

}
