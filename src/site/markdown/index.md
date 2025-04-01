## About CONGA Sling Plugin

wcm.io DevOps CONGA Plugin for [Apache Sling][sling].

[![Maven Central](https://img.shields.io/maven-central/v/io.wcm.devops.conga.plugins/io.wcm.devops.conga.plugins.sling)](https://repo1.maven.org/maven2/io/wcm/devops/conga/plugins/io.wcm.devops.conga.plugins.sling)


### Documentation

* [Usage][usage]
* [CONGA Extensions][extensions]
* [Combined JSON file for defining OSGi Configurations][osgi-config-combined-json]
* [API documentation][apidocs]
* [Changelog][changelog]


### Overview

This plugin extends [CONGA][conga] with:

* Manage OSGi configuration templates in [Combined JSON files][osgi-config-combined-json] or [Apache Sling Provisioning][sling-provisioning] file format
* Generate OSGi configurations in `.cfg.json` files (as used by [Apache Sling Configuration Installer Factory][sling-configuration-installer-factory-cfg-json])


### Further Resources

* [wcm.io CONGA training material with exercises](https://training.wcm.io/conga/)
* [adaptTo() 2015 Talk: CONGA - Configuration generation for Sling and AEM](https://adapt.to/2015/en/schedule/conga---configuration-generation-for-sling-and-aem.html)
* [adaptTo() 2017 Talk: Automate AEM Deployment with Ansible and wcm.io CONGA](https://adapt.to/2017/en/schedule/automate-aem-deployment-with-ansible-and-wcm-io-conga.html)



[usage]: usage.html
[extensions]: extensions.html
[osgi-config-combined-json]: osgi-config-combined-json.html
[apidocs]: conga-sling-plugin/apidocs/
[changelog]: changes.html
[conga]: https://devops.wcm.io/conga/
[sling]: http://sling.apache.org/
[sling-provisioning]: https://sling.apache.org/documentation/development/slingstart.html
[sling-configuration-installer-factory-cfg-json]: https://sling.apache.org/documentation/bundles/configuration-installer-factory.html#configuration-files-cfgjson
