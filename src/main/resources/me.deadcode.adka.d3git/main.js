"use strict";

var data = [4, 8, 15, 16, 23, 42];

var x = d3.scale.linear()
    .domain([0, d3.max(data)])
    .range([0, 420]);

d3.select(".chart")
    .selectAll("div") //the initial selection, to which we will join data. Think of the initial selection as declaring the elements you _want_ to exist.
      .data(data) //join the data to the selection
    .enter().append("div")
      .style("width", function(d) { return x(d) + "px"; })
      .text(function(d) { return d; });