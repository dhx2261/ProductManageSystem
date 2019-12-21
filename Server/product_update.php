<?php

require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Google\Cloud\BigQuery\BigQueryClient;

header("Access-Control-Allow-Methods: POST");
header('Content-type: multipart/form-data');

try {

    $bigQuery = new BigQueryClient([
        'projectId' => 'cc20192',
    ]);
    
    $name = $_POST['name'];
    (float) $price = $_POST['price'];
    $id = $_POST['id'];

    if (isset($_FILES['image'])) {
        $s3 = new Aws\S3\S3Client([
            'region'  => 'ap-northeast-2',
            'version' => '2006-03-01',
            'credentials' => [
                'key'    => "",
                'secret' => "",
            ]
        ]);
        date_default_timezone_set('Australia/Melbourne');
        $file = $_FILES['image']['tmp_name'];
        $keyName = date('YmdHis') . $id . '.png';
        do {
            $result = $s3->putObject([
                'Bucket' => 'cc20192',
                'Key'    => $keyName,
                'ContentType' => 'image/png',
                'SourceFile' => $file,
                'ACL' => 'public-read'
            ]);
        } while (!isset($result));
        if ($result["@metadata"]["statusCode"] == '200') {
            $url = $result->get("ObjectURL");
            $queryurl = ",imageURLs=\"$url\"";
            $query2 = "insert into Electronics.images values (\"$id\",\"$url\",\"$keyName\")";
            $queryJobConfig2 = $bigQuery->query($query2);
            $queryResults2 = $bigQuery->runQuery($queryJobConfig2);
            if ($queryResults2->isComplete()) {
                if ($queryResults2->info()['numDmlAffectedRows'] == 0) {
                    http_response_code(400);
                    die("Error happened when updaing image");
                }
            }
        }
    }

    //Update product name
    $sqldate=date('Y-m-d');
    if(isset($price)){
        $query = "update Electronics.electronics_name_price_image set name=\"$name\",prices_amountMin=$price$queryurl where ProdID=\"$id\"";
    }
    else{
        $query = "update Electronics.electronics_name_price_image set name=\"$name\" $queryurl where ProdID=\"$id\"";
    }
    $queryJobConfig = $bigQuery->query($query);
    $queryResults = $bigQuery->runQuery($queryJobConfig);
    //Create record in price history table
    if(isset($price)){
    $query4 = "insert into Electronics.product_pricehistory values (\"$id\",$price,\"$sqldate\")";
    $queryJobConfig4 = $bigQuery->query($query4);
    $queryResults4 = $bigQuery->runQuery($queryJobConfig4);
    }
    if ($queryResults->isComplete()) {
        if ($queryResults->info()['numDmlAffectedRows'] == 0) {
            http_response_code(400);
            die("Error happened when updating product database");
        } else {

            //Get updated product by id
            $query3 = "select * from Electronics.electronics_name_price_image where ProdID = \"$id\"";
            $queryJobConfig3 = $bigQuery->query($query3);
            $queryResults3 = $bigQuery->runQuery($queryJobConfig3);

            if ($queryResults3->isComplete()) {
                $info = $queryResults3->info();
                if ($info['totalRows'] == 0) {
                    http_response_code(400);
                    $myObj->status = "false";
                    $myObj->error = "No Result Found";
                    $myJSON = json_encode($myObj);
                    die($myJSON);
                } else {
                    header('Content-type: application/json');
                    $array = iterator_to_array($queryResults3->rows());
                    http_response_code(200);
                    echo json_encode($array, JSON_NUMERIC_CHECK);
                }
            }
        }
    }
} catch (Exception $e) {
    http_response_code(400);
    echo $e->getMessage();
}
