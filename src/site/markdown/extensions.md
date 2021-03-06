## CONGA - Extensions

The CONGA Sling Plugin extends CONGA using its [extensibility model][conga-extensibility].


### Provided Plugins

File plugins:

| Plugin name          | File name(s)        | File Header | Validator | Escaping | Post Processor |
|----------------------|---------------------|:-----------:|:---------:|:--------:|:--------------:|
| `osgi-config`        | .config             | X           |           | X        |                |
| `sling-provisioning` | .provisioning, .txt | X           | X         | X        | X              |

Please note: Files with the extension `txt` are only managed as provisioning files if the contain the string "[feature " (heuristic).


### Sling Provisioning Files

The Sling Provisioning Model file format is described on the [Sling Website][sling-slingstart]. It is a compact format that allows to define features with bundles and configurations for a Sling-based distribution. The CONGA Sling Plugin uses only the configurations and ignores all other part of the file.

You can use a Handlebars template to generate a provisioning file. When the post processor `sling-provisioning` is applied to this file the single Felix OSGi .config files are generated out of it and the provisioning file is deleted.


[conga-extensibility]: https://devops.wcm.io/conga/extensibility.html
[sling-slingstart]: https://sling.apache.org/documentation/development/slingstart.html
