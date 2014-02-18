<?php
$table_name = $_GET["table"];
$table_name = preg_replace('/[^a-zA-Z0-9_ %\[\]\.\(\)%&-]/s', '', $table_name);
mysql_connect("localhost","saadat38_adminsa","T7Y53SMC") or die("error");
mysql_select_db("saadat38_saadat");
mysql_query("SET NAMES 'utf8'");
$sth = mysql_query('SELECT * FROM $table_name');
$rows = array();
while($r = mysql_fetch_assoc($sth)) {
    $rows[] = $r;
}
print json_encode($rows);
?>