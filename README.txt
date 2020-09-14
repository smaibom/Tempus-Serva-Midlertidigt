To allow codeunits on the TempusServa platform go from frontpage of the webpage with admin rights: User drop down(Top right) -> Click Designer -> Modules drop down -> Configuration -> Select Codeunit in Group dropdown

Here set:
Filesystem folder for codeunits (Set the path of where the codeunits are located on server)
Reload codeunits with cache (Set to true when doing development)
Codeunit allow files (Set to true)
Add JAR library files to classpath (Set to true)
Load external codeunits (Set to true)

Upload codeunit jar file to selected path, from designer panel: Modules -> Clear Cache(Requires Load external codeunits to be true)

To attach a codeunit to a entity from the designer panel: Entities -> <Entity to add codeunit> -> Advanced -> Codeunit.
Here type <packagename>.<Class Name> of the code module to add. Attaching a codemodule to a entity use a class that extends CodeunitFormevents.
