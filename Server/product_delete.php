<?php
require __DIR__ . '/vendor/autoload.php';
use Google\Cloud\BigQuery\BigQueryClient;
header("Access-Control-Allow-Methods: DELETE");
header('Content-Type: application/json');

$id=$_GET['id'];

$projectId = 'cc20192';
$bigQuery = new BigQueryClient([
  'projectId' => $projectId,
]);
$query = "delete from Electronics.electronics_name_price_image where ProdID= \"$id\"";
$queryJobConfig = $bigQuery->query($query);
$queryResults = $bigQuery->runQuery($queryJobConfig);

if ($queryResults->isComplete()) {
  $info = $queryResults->info();
  if($info['numDmlAffectedRows']==0){
    http_response_code(400);
    $myObj->status = "false";
    $myObj->error="Product Not Found";
    $myJSON = json_encode($myObj);
    echo $myJSON;
  }
  else{
    http_response_code(200);
    $myObj->status = "true";
    $myJSON = json_encode($myObj);
    echo $myJSON;
  }
}
else{
  http_response_code(400);
  $myObj->status = "false";
  $myObj->error="Bigquery Error";
  $myJSON = json_encode($myObj);
  echo $myJSON;
}

?>