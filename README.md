Import Them
==========


A given directory structure grouping some eclipse projects to their working sets and Importing them by hand is a
cumbersome job. Especially when this is a regular job. To automate this work you can use the Import Them 
standard feature or write your own in groovy.


### install 

via [update site](http://baloise.github.io/importthem/updatesite/) 

Extra Groovy commpilers/Groovy Commpiler 2.0 Feature 

Groovy-Eclipse/Groovy-Eclipse Feature

Uncategorized/JDT Core patch for Groovy-Eclipse plugin

### Manual

In the preference page of eclipse you can see the currently used groovy script with the standard behaviour.

![Eclipse Preference Page](images/preferences.PNG)


When switching to Development you have Import Them project in your workspace and can edit the groovy script.

If you have a folder in your workspace (only possible in Package Explorer not in Project Explorer) with
a folder and project structure like:

![Folder Structure](images/projectTree.PNG)

With the standard behaviour where we will get two working sets sub1 and sub 2 where sub1 contains projects sub1proj1 and sub1proj2.
Workingset sub2 would contain sub2proj2. 
When in Development mode right clicking the projects folder and selecting Import them will execute a dry-run.


