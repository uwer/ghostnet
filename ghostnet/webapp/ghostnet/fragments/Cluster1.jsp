<html>
  
  
  <jsp:include page="/fragments/clusterHeader.jsp"/>
  <body>
  <article class="clearfix">
    

    
    <p>
  	<jsp:include page="/fragments/Cluster1/Cluster1.txt"/>
  	</p>
  	
  	<table border=0>
  	<tr><td>
  	<img class="hisrc" src="<%=request.getContextPath()%>/fragments/Cluster1/Cluster1.png" width="180" height="150" alt="Net type Cluster1">
    </td></tr></table>
    
    <jsp:include page="/fragments/clusterGraph.jsp"/>
    
  	</article>
  </body>
</html>
