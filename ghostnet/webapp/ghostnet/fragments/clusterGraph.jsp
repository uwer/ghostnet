


<br>



  	<%
        String chartData = (String)request.getAttribute("probChartData"); 
        String chartLabels = String.valueOf(request.getAttribute("probChartLabels")); 
        String probsToal = String.valueOf(request.getAttribute("probsTotal"));
        String probability  = String.valueOf(request.getAttribute("probsIndex"));
%>
This probability: <%=probability%> against a total match of <%=probsToal%>
<br>

<%      
        if (chartData != null && chartData.trim().length() > 2)
        {
                %>
                <p>
                According to the data provided the net specifications may also match the following to a lesser degree.
                </p>
  		<div id="chartDiv" style="width: 480px; height: 380px;">
  		</div>
  <script>
  var data = [{
  values:<%=chartData%>,
  labels:<%=chartLabels%>,
  type: 'pie'
  }];
  
  var layout = {
   height: 380,
   width: 480
  };
	Plotly.newPlot('chartDiv', data, layout);
  </script>
  		
  		
  	<%
  	}
  	%>
