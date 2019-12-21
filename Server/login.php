<?php
$servername = "";
$datausername = "";
$datapassword = "";
$db = "cc2019";
header("Access-Control-Allow-Methods: POST");
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    die("Method Not Allowed");
  }
// Create connection
$conn = new mysqli($servername, $datausername, $datapassword, $db);

$data = json_decode(file_get_contents('php://input'));
$inputusername=$data->username;
$inputpassword=$data->password;

if(empty($inputusername) or empty($inputpassword)){
    http_response_code(400);
    $myObj->status = "false";
    $myObj->error="Invalid Credential";
    $myJSON = json_encode($myObj);
    die($myJSON);
}

$sql = "SELECT password,type FROM users where username='$inputusername'";
$result = $conn->query($sql);
$row=mysqli_fetch_row($result);

// Check connection
if ($conn->connect_error) {
    http_response_code(404);
    die("error" . $conn->connect_error);
}

if(!empty($row[0]) and $row[0]==$inputpassword){
    http_response_code(200);
    $typestring=$row[1];
    $conn->close();
    $myObj->status = "true";
    $myObj->type = "$typestring";
    $myJSON = json_encode($myObj);
    echo $myJSON;
}
else{
    http_response_code(400);
    $conn->close();
    $myObj->status = "false";
    $myObj->error="Invalid Credential";
    $myJSON = json_encode($myObj);
    echo $myJSON;
}
?>