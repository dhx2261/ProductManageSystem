<?php
header("Access-Control-Allow-Methods: POST");
header('Content-Type: application/json');
$servername = "";
$username = "";
$password = "";
$db = "";

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    die("Method Not Allowed");
  }

// Create connection
$conn = new mysqli($servername, $username, $password, $db);

$data = json_decode(file_get_contents('php://input'));
$inputusername=$data->username;
$inputpassword=$data->password;
$inputtype=$data->type;

if(empty($inputusername) or empty($inputpassword) or empty($inputtype)){
    http_response_code(400);
    $myObj->status = "false";
    $myObj->error="Invalid Input";
    $myJSON = json_encode($myObj);
    die($myJSON);
}

$sql = "INSERT INTO users VALUES ('$inputusername', '$inputpassword', '$inputtype')";
$result = $conn->query($sql);
if(!$result){
    http_response_code(400);
    $myObj->status = "false";
    $myObj->error=$conn->error;
    $myJSON = json_encode($myObj);
    die($myJSON);
}
// echo  $conn->error;
else{
    http_response_code(200);
    $myObj->status = "true";
    $myJSON = json_encode($myObj);
    echo $myJSON;
}
?>