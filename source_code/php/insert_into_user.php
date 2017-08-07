<?php

	if($_SERVER['REQUEST_METHOD'] == 'POST') {
		echo "Post method";
		$host='localhost:3306';
		$uname='root';
		$pwd='';
		$db="sugarthrow";

		$con = mysqli_connect($host, $uname, $pwd, $db);
		 
		$table= $_REQUEST['table'];
		$username=$_REQUEST['userName'];
		$name=$_REQUEST['name'];
		$dob = $_REQUEST['dob'];
		$gender = $_REQUEST['gender'];
		$height = $_REQUEST['height'];
		$weight = $_REQUEST['weight'];
		$password = $_REQUEST['password'];
		$points = $_REQUEST['points'];

		$sql = "INSERT INTO $table (userName, name, gender, dob, height, weight, password, points) VALUES ('$username', '$name', '$gender', '$dob', $height, $weight, '$password', $points)"; 

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