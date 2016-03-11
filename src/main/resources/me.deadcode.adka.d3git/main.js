"use strict";

var data = [4, 8, 15, 16, 23, 42];

var width = 420,
    barHeight = 20;

var x = d3.scale.linear()
    .domain([0, d3.max(data)])
    .range([0, width]);

var chart = d3.select(".chart")
    .attr("width", width)
    .attr("height", barHeight * data.length);

var bar = chart.selectAll("g")
    .data(data)
    .enter().append("g")
    .attr("transform", function(d, i) { return "translate(0," + i * barHeight + ")"; }); //SVG elements need to be positioned absolutely with respect to the top-left corner


//Since there is exactly one rect and one text element per g element, we can append these elements directly to the g,
// without needing additional data joins. Data joins are only needed when creating a variable number of children
// based on data; here we are appending just one child per parent. The appended rects and texts inherit data
// from their parent g element, and thus we can use data to compute the bar width and label position.

bar.append("rect")
    .attr("width", x)
    .attr("height", barHeight - 1);

bar.append("text")
    .attr("x", function(d) { return x(d) - 3; })
    .attr("y", barHeight / 2)
    .attr("dy", ".35em")
    .text(function(d) { return d; });