"use strict";

//initialize as much as we can before the data is available
var width = 420,
    barHeight = 20;

var x = d3.scale.linear()
    .range([0, width]);

var chart = d3.select(".chart")
    .attr("width", width);

//then finish the rest in the callback once the data is downloaded
d3.csv("data.csv", type, function(error, data) {
    //asynchronous: code here runs after the download finishes

    console.log(data[0]);

    x.domain([0, d3.max(data, function(d) { return d.value; })]); //the second arg to max is an 'accessor function'

    chart.attr("height", barHeight * data.length);

    var bar = chart.selectAll("g") //'grouping element' in SVG
        .data(data)
        .enter().append("g")
        .attr("transform", function(d, i) { return "translate(0," + i * barHeight + ")"; });

    bar.append("rect")
        .attr("width", function(d) { return x(d.value); })
        .attr("height", barHeight - 1);

    bar.append("text")
        .attr("x", function(d) { return x(d.value) - 3; })
        .attr("y", barHeight / 2)
        .attr("dy", ".35em")
        .text(function(d) { return d.value; });
});
//code here runs while the file is downloading



//by default, all columns in TSV and CSV files are strings
//if you forget to convert strings to numbers, then JavaScript may not do what you expect
function type(d) {
    d.value = +d.value; // coerce to number
    return d;
}