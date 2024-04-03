[![Release Build Status](https://github.com/funfried/zonky-maven-plugin/actions/workflows/release_maven.yml/badge.svg)](https://github.com/funfried/zonky-maven-plugin/actions/workflows/release_maven.yml)
[![Linux Build Status](https://github.com/funfried/zonky-maven-plugin/actions/workflows/linux_maven.yml/badge.svg)](https://github.com/funfried/zonky-maven-plugin/actions/workflows/linux_maven.yml)
[![Windows Build Status](https://github.com/funfried/zonky-maven-plugin/actions/workflows/windows_maven.yml/badge.svg)](https://github.com/funfried/zonky-maven-plugin/actions/workflows/windows_maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/45f6c8ab0e014809ba98276a709197f0)](https://www.codacy.com/gh/funfried/zonky-maven-plugin/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=funfried/zonky-maven-plugin&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://img.shields.io/maven-central/v/de.funfried.maven.plugins/zonky-maven-plugin)](https://repo1.maven.org/maven2/de/funfried/maven/plugins/zonky-maven-plugin/)
[![GitHub All Releases](https://img.shields.io/github/downloads/funfried/zonky-maven-plugin/total)](https://github.com/funfried/zonky-maven-plugin/releases)
[![GitHub issues](https://img.shields.io/github/issues/funfried/zonky-maven-plugin)](https://github.com/funfried/zonky-maven-plugin/issues)
[![Apache License, Version 2.0](https://img.shields.io/github/license/funfried/zonky-maven-plugin)](http://funfried.github.io/zonky-maven-plugin/licenses.html)
[![Follow Me On X/Twitter](https://img.shields.io/twitter/follow/funfried84?style=social)](https://twitter.com/funfried84)

Zonky Plugin for Maven
================================================

What is Zonky Maven Plugin?
----------------------------------------------------
This plugin helps you to start an embedded [PostgreSQL](https://www.postgresql.org/) database with the help of the [Zonky Embedded Postgres](https://github.com/zonkyio/embedded-postgres) project.
This plugin is intended to help you running your database migration scripts (e.g. Liquibase or Flyway) or other database related tasks during build time against a production near database.

### Features
*   Start and stop an embedded Postgres database during any maven lifecycle phase
*   Create your database schema when the embedded database starts up
*   Use a fixed or a random port for the embedded database (the random port will be written into a Maven variable `zonky.port` besides others)
*   Customize the work and data directory of the embedded database

Compatibility
-------------
Compatible with JDK 8+ and Maven >= 3.9.5

Downloads
---------
The latest version should always be available in Maven Central, but just in case you can find the download links [here](http://funfried.github.io/zonky-maven-plugin/downloads.html).

Known issues
------------
Please check the open [GitHub Issues](/../../issues) and see [here](http://funfried.github.io/zonky-maven-plugin/known_issues.html)

Feedback
--------
Provide defects, requests for enhancements or general feedback at the [GitHub issues](/../../issues) page.
Please check the known issues (see above) before you create an issue and check if your issue also appears in the latest development version (download links can be found [here](http://funfried.github.io/zonky-maven-plugin/downloads.html)).

Changelog
---------
You can find the changelog of all versions [here](http://funfried.github.io/zonky-maven-plugin/changes-report.html)

Licensing
---------
This plugin is licensed under the [Apache License, Version 2.0](http://funfried.github.io/zonky-maven-plugin/licenses.html).
This plugin uses third-party libraries, which are needed to provide its functionality, please check their licenses [here](https://funfried.github.io/zonky-maven-plugin/dependencies.html).

Support
---------
Keep this project alive by supporting it:
one-time [![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=926F5XBCTK2LQ&source=url) or [![Patreon!](/src/main/site/resources/imgs/logos/become_a_patron_button.png)](https://www.patreon.com/funfried) or become a [![Sponsor](https://img.shields.io/static/v1?label=Sponsor&message=%E2%9D%A4&logo=GitHub&color=%23fe8e86)](https://github.com/sponsors/funfried) or just [![Buy me a drink](https://img.buymeacoffee.com/button-api/?text=Buy%20me%20a%20drink&emoji=%F0%9F%A5%83&slug=funfried&button_colour=5F7FFF&font_colour=ffffff&font_family=Cookie&outline_colour=000000&coffee_colour=FFDD00)](https://www.buymeacoffee.com/funfried)

If you can't or don't want to spend money you can also just [![say thanks](https://img.shields.io/static/v1?label=say&message=thanks&color=green&style=for-the-badge&logo=handshake)](https://saythanks.io/to/funfried)
