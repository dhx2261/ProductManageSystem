<?php
   include("fusioncharts.php");
   require __DIR__ . '/vendor/autoload.php';
   use Google\Cloud\BigQuery\BigQueryClient;
   header("Access-Control-Allow-Methods: GET");
?>

<html>

<head>
    <title>Price History</title>
    <script src="https://cdn.fusioncharts.com/fusioncharts/latest/fusioncharts.js"></script>
    <script src="https://cdn.fusioncharts.com/fusioncharts/latest/themes/fusioncharts.theme.fusion.js"></script>
</head>

<body>
    <?php
    $id=$_GET['id'];
    if(empty($id)){
        http_response_code(400);
        $myObj->status = "false";
        $myObj->error="Invalid Input";
        $myJSON = json_encode($myObj);
        die($myJSON);
    }
        $arrChartConfig = array(
            "chart" => array(
                "caption" => "Price history for product $id",
                "subCaption" => "",
                "xAxisName" => "Date",
                "yAxisName" => "Price",
                "numberSuffix" => "AUD",
                "theme" => "fusion"
            )
        );
    $projectId = 'cc20192';
    $bigQuery = new BigQueryClient([
        'projectId' => $projectId,
    ]);
    $query = "select prices_dateSeen as label, prices_amountMin as value from Electronics.product_pricehistory where ProdID=\"$id\" order by prices_dateSeen";
    $queryJobConfig = $bigQuery->query($query);
    $queryResults = $bigQuery->runQuery($queryJobConfig);
    if ($queryResults->isComplete()) {
        $info = $queryResults->info();
        if($info['totalRows']==0){
            http_response_code(400);
            $myObj->status = "false";
            $myObj->error="No Product Found";
            $myJSON = json_encode($myObj);
            die($myJSON);
        }
        else{
        $rows=$queryResults->rows();
        $arrChartConfig["data"] = array();
        foreach($rows as $row){
            array_push($arrChartConfig["data"], array(
                "label" => date("d/m/Y", strtotime($row["label"])),
                "value" => $row["value"]
                )
            );
        }
        }
    }
    $jsonEncodedData = json_encode($arrChartConfig);
    $Chart = new FusionCharts("line", "ex1" , "100%", "400", "chart-container", "json", $jsonEncodedData);
    $Chart->render();
    ?>
    <div id="chart-container">Chart will render here!</div>
</body>
</html>