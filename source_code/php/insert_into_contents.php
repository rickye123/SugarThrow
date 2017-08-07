<?php

	if($_SERVER['REQUEST_METHOD'] == 'POST') {
		echo "Post method";
		$host='localhost:3306';
		$uname='root';
		$pwd='';
		$db="sugarthrow";

		$con = mysqli_connect($host, $uname, $pwd, $db);
		 
		$table= $_REQUEST['table'];
		$userId=$_REQUEST['userId'];
		$theDate=$_REQUEST['theDate'];
		$sugar = $_REQUEST['sugar'];
		$calories = $_REQUEST['calories'];
		$fat = $_REQUEST['fat'];
		$saturates = $_REQUEST['saturates'];
		$carbs = $_REQUEST['carbs'];
		$salt = $_REQUEST['salt'];
		$protein = $_REQUEST['protein'];

		$sql = "INSERT INTO $table (userId, theDate, sugar, calories, fat, saturates, carbs, salt, protein) VALUES ($userId, '$theDate', $sugar, $calories, $fat, $saturates, $carbs, $salt, $protein)"; 

		if ($con->query($sql) === TRUE) {
    		echo "New record created successfully";
		} else {
    		echo "Error: " . $sql . "<br>" . $con->error;
		}

 
    	mysqli_close($con);
	}
	else {
		echo "Error: Method was not a post method"; 
	}

	
?>