<?php

require 'vendor/autoload.php';

use Aws\S3\S3Client;
use Google\Cloud\BigQuery\BigQueryClient;

header("Access-Control-Allow-Methods: POST");
header('Content-type: multipart/form-data');
try {
    if (!isset($_POST['name']) or !isset($_POST['price']) or !is_numeric($_POST['price']) or !isset($_FILES['image'])) {
        http_response_code(400);
        die("Invalid request body");
    }

    $name = $_POST['name'];
    (float) $price = $_POST['price'];
    $id = uniqid(rand(), true);

    $s3 = new Aws\S3\S3Client([
        'region'  => 'ap-northeast-2',
        'version' => '2006-03-01',
        'credentials' => [
            'key'    => "",
            'secret' => "",
        ]
    ]);


    if (isset($_FILES['image'])) {
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
            $bigQuery = new BigQueryClient([
                'projectId' => 'cc20192',
            ]);
            $sqldate=date('Y-m-d');
            $query = "insert into Electronics.electronics_name_price_image values(\"$id\",\"$name\",$price,null,\"$sqldate\",\"$url\")";
            $queryJobConfig = $bigQuery->query($query);
            $queryResults = $bigQuery->runQuery($queryJobConfig);
            //Create record in image table
            $query2 = "insert into Electronics.images values (\"$id\",\"$url\",\"$keyName\")";
            $queryJobConfig2 = $bigQuery->query($query2);
            $queryResults2 = $bigQuery->runQuery($queryJobConfig2);
            //Create reconrd in price history table
            $query3 = "insert into Electronics.product_pricehistory values (\"$id\",$price,\"$sqldate\")";
            $queryJobConfig3 = $bigQuery->query($query3);
            $queryResults3 = $bigQuery->runQuery($queryJobConfig3);
            if ($queryResults->isComplete() and $queryResults2->isComplete() and $queryResults3->isComplete()) {
                if ($queryResults->info()['numDmlAffectedRows'] == 0 or $queryResults2->info()['numDmlAffectedRows'] == 0 or $queryResults3->info()['numDmlAffectedRows'] == 0) {
                    http_response_code(400);
                    die("Error happened when updating product database");
                } else {
                    header('Content-type: application/json');
                    http_response_code(200);
                    $myObj->id = $id;
                    $myObj->name = $name;
                    $myObj->price = $price;
                    $myObj->imageurl = $url;
                    echo json_encode($myObj,JSON_NUMERIC_CHECK);
                }
            }
        } else {
            http_response_code(400);
            die("Error happend when uploading image");
        }
    }
} catch (Exception $e) {
    http_response_code(400);
    echo $e->getMessage();
}
