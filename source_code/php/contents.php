<?php 
	//Creating a connection
	$con = mysqli_connect("localhost:3306","root","","sugarthrow");
	 
    if (mysqli_connect_errno())
    {
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
	/*Get the id of the last visible item in the RecyclerView from the request and store it in a variable. For the first request, id will be zero.*/	
	$id = (isset($_GET['contentId']) ? $_GET['contentId'] : null);
	
	$sql= "Select * from Contents";
	
	$result = mysqli_query($con ,$sql);
	
	if(mysqli_num_rows($result) > 0) {
	
		while ($row = mysqli_fetch_assoc($result)) {
			$array[] = $row;	
		}
		
	}
	else {
		$array = array(
		   array('contentId' => "null"),
		);
	}

	header('Content-Type:Application/json');
	echo json_encode($array);
 
    mysqli_free_result($result);
 
    mysqli_close($con);
  
 ?>