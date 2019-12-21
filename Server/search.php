<?php
require __DIR__ . '/vendor/autoload.php';
use Google\Cloud\BigQuery\BigQueryClient;
header('Content-Type: application/json');
header("Access-Control-Allow-Methods: GET");

$keyword=$_GET['keyword'];
$id=$_GET['id'];
$category=$_GET['category'];

if(empty($keyword)&&empty($id)){
    http_response_code(400);
    $myObj->status = "false";
    $myObj->error="Invalid Input";
    $myJSON = json_encode($myObj);
    die($myJSON);
}

$projectId = 'cc20192';
$bigQuery = new BigQueryClient([
    'projectId' => $projectId,
]);
if(isset($keyword) and !isset($category)){
$query = "select * from Electronics.electronics_name_price_image where lower(name) like lower(\"%$keyword%\")";
}
elseif(isset($id)){
    $query = "select * from Electronics.electronics_name_price_image where ProdID = \"$id\"";
}
elseif(isset($category) and isset($keyword)){
    $query="select distinct a.ProdID,a.name,a.prices_amountMin, a.imageURLs from Electronics.electronics_name_price_image as a join Electronics.electronics_ID_category as b on a.ProdID=b.ProdID where lower(categories) like lower(\"%$category%\") and lower(a.name) like lower(\"%$keyword%\")";
}
$queryJobConfig = $bigQuery->query($query);
$queryResults = $bigQuery->runQuery($queryJobConfig);

if ($queryResults->isComplete()) {
    $info = $queryResults->info();
    if($info['totalRows']==0){
        http_response_code(400);
        $myObj->status = "false";
        $myObj->error="No Result Found";
        $myJSON = json_encode($myObj);
        die($myJSON);
    }
    else{
    $array = iterator_to_array($queryResults->rows());
    http_response_code(200);
    echo json_encode($array,JSON_NUMERIC_CHECK);
    }
} else {
    http_response_code(400);
    $myObj->status = "false";
    $myObj->error="Bigquery Error";
    $myJSON = json_encode($myObj);
    throw new Exception($myJSON);
}

?>