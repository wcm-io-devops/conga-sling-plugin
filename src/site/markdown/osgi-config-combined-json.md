## Combined JSON file for defining OSGi Configurations

Similar to the (deprecated) [Apache Sling Provisioning File Format][sling-provisioning] it is possible to define a set of OSGi configurations using a CONGA template in a single JSON file which contains a set of OSGi configurations and [repoinit][sling-repoinit] statements, optionally mapped to run modes.

From this combined JSON file, CONGA generates individually `.cfg.json` files as used by [Apache Sling Configuration Installer Factory][sling-configuration-installer-factory-cfg-json]. With this approach it is easy to define all configurations in one file, and add/remove configurations based on CONGA environment variables using Handlebars logic.

### JSON file example

```json
{
  "configurations": {
    "my.pid": {
      "prop1": "value1",
      "prop2": [1,2,3],
      "prop3": true
    }
  },

  "configurations:dev": {
    "my.pid2": {
      "prop1": "value-for-dev"
    }
  },

  "configurations:publish,prod": {
    "my.pid2": {
      "prop1": "value-for-publish-prod"
    }
  }

  "repoinit": [
    "create path /repoinit/test1",
    "create path /repoinit/test2"
  ],
  "repoinit:dev": [
    "create service user dev-user"
  ]
}
```

The following keys are allowed on toplevel of the JSON file:

* `configurations` - Configurations that are always active
* `configurations:runmode` - Configurations that are active only for the given run mode.
* `repoinit` - List of repoinit statements to be always applied
* `repoinit:runmode` - List of repoinit statements to be applied for the given run mode.

`runmode` can be a comma-separate strings, e.g. `prod,publish`. In this case all given run modes have to be active. On AEM as a Cloud Service, only the [officially supported run modes][aemaacs-runmodes] are allowed.

You can use comments in the JSON file, they are stripped out when generating the `.cfg.json` files.

Real world example with HBS template: [wcm-io-samples-aem-cms-config.osgiconfig.json.hbs](https://github.com/wcm-io/io.wcm.samples/blob/develop/config-definition/src/main/templates/wcm-io-samples-aem-cms/wcm-io-samples-aem-cms-config.osgiconfig.json.hbs)

[sling-provisioning]: https://sling.apache.org/documentation/development/slingstart.html
[sling-repoinit]: https://sling.apache.org/documentation/bundles/repository-initialization.html
[sling-configuration-installer-factory-cfg-json]: https://sling.apache.org/documentation/bundles/configuration-installer-factory.html#configuration-files-cfgjson
[aemaacs-runmodes]: https://experienceleague.adobe.com/docs/experience-manager-cloud-service/content/implementing/deploying/overview.html?lang=en#runmodes