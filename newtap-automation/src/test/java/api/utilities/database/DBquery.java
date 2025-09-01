package api.utilities.database;

import java.sql.ResultSet;



public class DBquery {

//    String Host = "stage-master-db.stg.dreamplug.net";
//    String database = "bureau_service";


    public String getuserdetails(String las_crn){

        String query = "SET @new_request_id = UUID();\n" +
                "\n" +
                "SET @new_record_id = UUID();\n" +
                "\n" +
                "SET @new_document_id1 = UUID();\n" +
                "\n" +
                "SET @new_document_id2 = UUID();\n" +
                "\n" +
                "SET @new_document_id3 = UUID();\n" +
                "\n" +
                "SET @new_document_id4 = UUID();\n" +
                "\n" +
                "SET @new_tenant_id = 'NB_PARF_000';\n" +
                "\n" +
                "SET @new_partner_id = 'CRED';\n" +
                "\n" +
                "SET @configuration_id = '69e09168-ea41-40b2-b02f-99e26c5b8dd3';\n" +
                "\n" +
                "-- Set newtap_crn value\n" +
                "SET @newtap_crn = '"+las_crn+"';\n" +
                "\n" +
                "-- Step 1: Duplicate bureau_requests\n" +
                "INSERT INTO\n" +
                "    `bureau_requests` (\n" +
                "        `id`,\n" +
                "        `request_id`,\n" +
                "        `tenant_id`,\n" +
                "        `partner_id`,\n" +
                "        `idempotency_key`,\n" +
                "        `configuration_id`,\n" +
                "        `account_holder_reference_id`,\n" +
                "        `request_data`,\n" +
                "        `status`,\n" +
                "        `sub_status`,\n" +
                "        `error_code`,\n" +
                "        `version`,\n" +
                "        `created_at`,\n" +
                "        `updated_at`,\n" +
                "        `created_by`,\n" +
                "        `updated_by`\n" +
                "    )\n" +
                "SELECT\n" +
                "    NULL, -- Assuming `id` is auto-incremented, set it to NULL\n" +
                "    @new_request_id,\n" +
                "    @new_tenant_id,\n" +
                "    @new_partner_id,\n" +
                "    UUID(), -- Generate a new idempotency_key\n" +
                "    @configuration_id,\n" +
                "    @newtap_crn,\n" +
                "    `request_data`,\n" +
                "    `status`,\n" +
                "    `sub_status`,\n" +
                "    `error_code`,\n" +
                "    `version`,\n" +
                "    NOW(), -- Set created_at to current timestamp\n" +
                "    NOW(), -- Set updated_at to current timestamp\n" +
                "    `created_by`,\n" +
                "    `updated_by`\n" +
                "FROM `bureau_requests`\n" +
                "WHERE\n" +
                "    `account_holder_reference_id` = '46316a51-53f2-4b90-ae2c-8af0f48b09db'\n" +
                "    AND `status` = 'COMPLETED';\n" +
                "\n" +
                "-- Step 2: Duplicate records\n" +
                "INSERT INTO\n" +
                "    `records` (\n" +
                "        `id`,\n" +
                "        `record_id`,\n" +
                "        `request_id`,\n" +
                "        `tenant_id`,\n" +
                "        `partner_id`,\n" +
                "        `account_holder_reference_id`,\n" +
                "        `provider_id`,\n" +
                "        `provider_instance_id`,\n" +
                "        `validity`,\n" +
                "        `data`,\n" +
                "        `created_at`,\n" +
                "        `updated_at`,\n" +
                "        `created_by`,\n" +
                "        `updated_by`,\n" +
                "        `version`\n" +
                "    )\n" +
                "SELECT\n" +
                "    NULL, -- Assuming `id` is auto-incremented, set it to NULL\n" +
                "    @new_record_id,\n" +
                "    @new_request_id,\n" +
                "    @new_tenant_id,\n" +
                "    @new_partner_id,\n" +
                "    @newtap_crn,\n" +
                "    `provider_id`,\n" +
                "    `provider_instance_id`,\n" +
                "    `validity`,\n" +
                "    `data`,\n" +
                "    NOW(), -- Set created_at to current timestamp\n" +
                "    NOW(), -- Set updated_at to current timestamp\n" +
                "    `created_by`,\n" +
                "    `updated_by`,\n" +
                "    `version`\n" +
                "FROM `records`\n" +
                "WHERE\n" +
                "    `account_holder_reference_id` = '46316a51-53f2-4b90-ae2c-8af0f48b09db';\n" +
                "\n" +
                "-- Step 3 [CRIF]: Duplicate documents(2 document entries - json & xml file)\n" +
                "INSERT INTO\n" +
                "    `documents` (\n" +
                "        `id`,\n" +
                "        `document_id`,\n" +
                "        `tenant_id`,\n" +
                "        `partner_id`,\n" +
                "        `record_id`,\n" +
                "        `account_holder_reference_id`,\n" +
                "        `validity`,\n" +
                "        `path`,\n" +
                "        `created_at`,\n" +
                "        `updated_at`,\n" +
                "        `created_by`,\n" +
                "        `updated_by`,\n" +
                "        `version`,\n" +
                "        `file_name`\n" +
                "    )\n" +
                "SELECT\n" +
                "    NULL, -- Assuming `id` is auto-incremented, set it to NULL\n" +
                "    @new_document_id1,\n" +
                "    @new_tenant_id,\n" +
                "    @new_partner_id,\n" +
                "    @new_record_id,\n" +
                "    @newtap_crn,\n" +
                "    `validity`,\n" +
                "    `path`,\n" +
                "    NOW(), -- Set created_at to current timestamp\n" +
                "    NOW(), -- Set updated_at to current timestamp\n" +
                "    `created_by`,\n" +
                "    `updated_by`,\n" +
                "    `version`,\n" +
                "    `file_name`\n" +
                "FROM `documents`\n" +
                "WHERE\n" +
                "    `account_holder_reference_id` = '46316a51-53f2-4b90-ae2c-8af0f48b09db'\n" +
                "    AND `file_name` = 'crif_raw_report.xml'\n" +
                "LIMIT 1;\n" +
                "\n" +
                "INSERT INTO\n" +
                "    `documents` (\n" +
                "        `id`,\n" +
                "        `document_id`,\n" +
                "        `tenant_id`,\n" +
                "        `partner_id`,\n" +
                "        `record_id`,\n" +
                "        `account_holder_reference_id`,\n" +
                "        `validity`,\n" +
                "        `path`,\n" +
                "        `created_at`,\n" +
                "        `updated_at`,\n" +
                "        `created_by`,\n" +
                "        `updated_by`,\n" +
                "        `version`,\n" +
                "        `file_name`\n" +
                "    )\n" +
                "SELECT\n" +
                "    NULL, -- Assuming `id` is auto-incremented, set it to NULL\n" +
                "    @new_document_id2,\n" +
                "    @new_tenant_id,\n" +
                "    @new_partner_id,\n" +
                "    @new_record_id,\n" +
                "    @newtap_crn,\n" +
                "    `validity`,\n" +
                "    `path`,\n" +
                "    NOW(), -- Set created_at to current timestamp\n" +
                "    NOW(), -- Set updated_at to current timestamp\n" +
                "    `created_by`,\n" +
                "    `updated_by`,\n" +
                "    `version`,\n" +
                "    `file_name`\n" +
                "FROM `documents`\n" +
                "WHERE\n" +
                "    `account_holder_reference_id` = '46316a51-53f2-4b90-ae2c-8af0f48b09db'\n" +
                "    AND `file_name` = 'crif_report_formatted.json'\n" +
                "LIMIT 1;\n" +
                "\n" +
                "-- Step 4 [EXPERIAN]: Duplicate documents(2 document entries - json & xml file)\n" +
                "INSERT INTO\n" +
                "    `documents` (\n" +
                "        `id`,\n" +
                "        `document_id`,\n" +
                "        `tenant_id`,\n" +
                "        `partner_id`,\n" +
                "        `record_id`,\n" +
                "        `account_holder_reference_id`,\n" +
                "        `validity`,\n" +
                "        `path`,\n" +
                "        `created_at`,\n" +
                "        `updated_at`,\n" +
                "        `created_by`,\n" +
                "        `updated_by`,\n" +
                "        `version`,\n" +
                "        `file_name`\n" +
                "    )\n" +
                "SELECT\n" +
                "    NULL, -- Assuming `id` is auto-incremented, set it to NULL\n" +
                "    @new_document_id3,\n" +
                "    @new_tenant_id,\n" +
                "    @new_partner_id,\n" +
                "    @new_record_id,\n" +
                "    @newtap_crn,\n" +
                "    `validity`,\n" +
                "    `path`,\n" +
                "    NOW(), -- Set created_at to current timestamp\n" +
                "    NOW(), -- Set updated_at to current timestamp\n" +
                "    `created_by`,\n" +
                "    `updated_by`,\n" +
                "    `version`,\n" +
                "    `file_name`\n" +
                "FROM `documents`\n" +
                "WHERE\n" +
                "    `account_holder_reference_id` = '46316a51-53f2-4b90-ae2c-8af0f48b09db'\n" +
                "    AND `file_name` = 'experian_raw_report.xml'\n" +
                "LIMIT 1;\n" +
                "\n" +
                "INSERT INTO\n" +
                "    `documents` (\n" +
                "        `id`,\n" +
                "        `document_id`,\n" +
                "        `tenant_id`,\n" +
                "        `partner_id`,\n" +
                "        `record_id`,\n" +
                "        `account_holder_reference_id`,\n" +
                "        `validity`,\n" +
                "        `path`,\n" +
                "        `created_at`,\n" +
                "        `updated_at`,\n" +
                "        `created_by`,\n" +
                "        `updated_by`,\n" +
                "        `version`,\n" +
                "        `file_name`\n" +
                "    )\n" +
                "SELECT\n" +
                "    NULL, -- Assuming `id` is auto-incremented, set it to NULL\n" +
                "    @new_document_id4,\n" +
                "    @new_tenant_id,\n" +
                "    @new_partner_id,\n" +
                "    @new_record_id,\n" +
                "    @newtap_crn,\n" +
                "    `validity`,\n" +
                "    `path`,\n" +
                "    NOW(), -- Set created_at to current timestamp\n" +
                "    NOW(), -- Set updated_at to current timestamp\n" +
                "    `created_by`,\n" +
                "    `updated_by`,\n" +
                "    `version`,\n" +
                "    `file_name`\n" +
                "FROM `documents`\n" +
                "WHERE\n" +
                "    `account_holder_reference_id` = '46316a51-53f2-4b90-ae2c-8af0f48b09db'\n" +
                "    AND `file_name` = 'experian_report_formatted.json'\n" +
                "LIMIT 1;";

        ResultSet rs = DBhost.runQuery(query);

        return query;
    }

}
