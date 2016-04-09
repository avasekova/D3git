<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Visualizations</title>

    <link href="<c:url value="/me/deadcode/adka/d3git/css/styles.css" />" rel="stylesheet">
</head>
<body>

    <!-- TODO js libraries management -->
    <!-- LOAD D3 -->
    <script type="text/javascript" src="<c:url value="/me/deadcode/adka/d3git/scripts/d3.v3.js" />"></script>
    <!-- LOAD D3-TIP FOR TOOLTIPS -->
    <script type="text/javascript" src="<c:url value="/me/deadcode/adka/d3git/scripts/d3.v3.js" />"></script>
    <!-- LOAD ES js client -->
    <script type="text/javascript" src="<c:url value="/me/deadcode/adka/d3git/scripts/elasticsearch.js" />"></script>

    <svg class="chart" xmlns="http://www.w3.org/2000/svg"></svg>

    <!-- TODO display reasonable errors (repo not found etc.) -->


    <script type="text/javascript">
        var data = ${data};        <!-- Note: "${data}" won't work for Strings, for some reason it needs to be '' -->
    </script>

    <script type="text/javascript" src="<c:url value="/me/deadcode/adka/d3git/scripts/main.js" />">//empty tags do not work in some browsers</script>

    <!--c:out value="${param.repositoryPath}"/-->
    <!--c:out value="${param.location}"/-->


</body>
</html>
