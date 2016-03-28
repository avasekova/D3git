<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Visualizations</title>

    <link href="<c:url value="/me/deadcode/adka/d3git/css/styles.css" />" rel="stylesheet">
</head>
<body>

    <!-- LOAD D3 -->
    <script src="<c:url value="http://d3js.org/d3.v3.min.js" />"></script>
    <!-- LOAD D3-TIP FOR TOOLTIPS -->
    <script src="<c:url value="http://labratrevenge.com/d3-tip/javascripts/d3.tip.v0.6.3.js" />"></script>

    <svg class="chart" xmlns="http://www.w3.org/2000/svg"></svg>

    <!-- TODO display reasonable errors (repo not found etc.) -->


    <script type="text/javascript">
        var data = ${data};        <!-- Note: "${data}" won't work for Strings, for some reason it needs to be '' -->
    </script>

    <script type="text/javascript" src="<c:url value="/me/deadcode/adka/d3git/main.js" />">//empty tags do not work in some browsers</script>

    <!--c:out value="${param.repositoryPath}"/-->
    <!--c:out value="${param.location}"/-->


</body>
</html>
