
# Sync Gateway request:  Create a new user via a direct Sync Gateway request
$method = "PUT"
$resource = "http://192.168.191.210:4985/gw/_user/skipxxx"
$body = "{
    ""name"":""skipxxx"",
    ""password"":""pass"",
    ""admin_channels"":[""skipxxx""],
    ""admin_roles"":[""editor""],
    ""disabled"":false
}"
echo $body;
Invoke-RestMethod -Method $method -Uri $resource -Body $body -ContentType 'application/json'


# Sync Gateway request:  Delete a user
$method = "DELETE"
$resource = "http://192.168.191.210:4985/gw/_user/skipxxx"
Invoke-RestMethod -Method $method -Uri $resource -ContentType 'application/json'


# Sync Gateway request:  Get used infos
$method = "GET"
$resource = "http://192.168.191.210:4985/gw/_user/skipxxx"
Invoke-RestMethod -Method $method -Uri $resource


# :  run the index.php to get infos
$method = "GET"
$resource = "http://192.168.191.210:80"
#Invoke-RestMethod -Method $method -Uri $resource -ContentType 'application/json'


# sending a 
$method = "PUT"
$resource = "http://192.168.191.210:80"
$body = "{
    ""method"":""theput"",
    ""name"":""skip"",
    ""password"":""pass""
}"

# Invoke-RestMethod -Method $method -Uri $resource -Body $body -ContentType 'application/json'



# HTTP authentication
$resource = "http://192.168.191.210:4985/gw/_session"
$method = "POST"
$body = "{
    ""name"":""skip"",
    ""password"":""pass""
}"
$securepassword = ConvertTo-SecureString "pass" -AsPlainText -Force
$credentials = New-Object System.Management.Automation.PSCredential("skip", $securepassword)
Invoke-WebRequest -Method $method -Uri $resource -Credential $credentials -Body $body  -ContentType application/json


# resync the db data with the new sync funciton
$resource = "http://192.168.191.210:4985/gw/_resync"
$method = "POST"
Invoke-RestMethod -Method $method -Uri $resource



# create a document by REST API
$now = $(get-date);
$resource = "http://192.168.191.210:4985/gw/"
$method = "POST"
$body = "{
    ""type"":""FromConsoleType3"",
    ""title"":""Document from Console3"",
    ""checked"":""true"",
    ""created_at"":""$now"",
    ""creator"":""skipxxx"",
    ""writers"":[""skipxxx""]
}"
$securepassword = ConvertTo-SecureString "pass" -AsPlainText -Force
$credentials = New-Object System.Management.Automation.PSCredential("skipxxx", $securepassword)
Invoke-WebRequest -Uri $resource -Method POST -Credential $credentials -Body $body  -ContentType application/json
