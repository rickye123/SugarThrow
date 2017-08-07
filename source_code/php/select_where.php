<?php 
	//Creating a connection
	$con = mysqli_connect("localhost:3306","root","","sugarthrow");
	 
    if (mysqli_connect_errno())
    {
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
	/*Get the id of the last visible item in the RecyclerView from the request and store it in a variable. For the first request, id will be zero.*/

	$table = $_REQUEST['table'];
	$tableRow = $_REQUEST['row'];
	$value = $_REQUEST['value'];
	$other = $_REQUEST['other'];
	$otherVal = $_REQUEST['otherVal'];
	$identifier = $_REQUEST['identifier']; 
	
	$sql= "SELECT * FROM $table WHERE $tableRow = '$value' AND $other = '$otherVal'";
		
	$result = mysqli_query($con ,$sql);

	if(mysqli_num_rows($result) > 0) {
	
		while ($row = mysqli_fetch_assoc($result)) {
			
			$array[] = $row;
			
		}
		header('Content-Type:Application/json');
		echo json_encode($array);
	}
	else {
		header('Content-Type:Application/json');
		$array = array(
		   array($identifier => "null"),
		);
		echo json_encode($array);
	}


 
    mysqli_free_result($result);
 
    mysqli_close($con);
  
 ?>