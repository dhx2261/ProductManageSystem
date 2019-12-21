<?php
$method = $_SERVER['REQUEST_METHOD'];
switch (@parse_url($_SERVER['REQUEST_URI'])['path']) {
    case '/user/login':
        require 'login.php';
        break;
    case '/user/register':
        require 'register.php';
        break;

    case '/product':
        if ($method == 'GET') {
            require 'search.php';
        } elseif ($method == 'DELETE') {
            require 'product_delete.php';
        } elseif ($method == 'POST') {
            if ($_SERVER['HTTP_ACTION'] == "create") {
                require 'product_create.php';
            } else if ($_SERVER['HTTP_ACTION'] == "update") {
                require 'product_update.php';
            }
        } else {
            http_response_code(405);
            die("Method Not Allowed");
        }
        break;

    case '/chart':
        require 'chart.php';
        break;
    default:
        http_response_code(404);
        exit('Not Found');
}
