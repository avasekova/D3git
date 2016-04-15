"use strict";

//initialize as much as we can before the data is available
var margin = {top: 20, right: 30, bottom: 30, left: 40};
var chartWidth = 1200 - margin.left - margin.right;
var chartHeight = 500 - margin.top - margin.bottom;

var x = d3.scale.ordinal()
    .rangeRoundBands([0, chartWidth], .1);

var y = d3.scale.linear()
    .range([chartHeight, 0]); //the origin of SVG’s coordinate system is in the top-left corner

var xAxis = d3.svg.axis()
    .scale(x) //bind the axis to a scale
    .orient("bottom");
//the resulting xAxis object can be used to render multiple axes by repeated application using selection.call

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .tickFormat(d3.format("d")); //integer ticks


var chart = d3.select(".chart")
    .attr("width", chartWidth + margin.left + margin.right)
    .attr("height", chartHeight + margin.top + margin.bottom)
    .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
//any elements subsequently added to chart will inherit the margins

//init tooltips
var tip = d3.tip()
    .attr("class", "d3-tip")
    .offset(function() {
        return [-this.getBBox().height / 2, 5];  //offset from the calculated position (move [T, L] from [top, left])
    })
    .direction("e") // Position the tooltip to the right of a target element
    .html(function(d) {
        return "x: " + d.key_as_string.substring(0, 10) + "<br/>" + "y: " + d.y.value;
    });
chart.call(tip);


//load data from ES
var client = new elasticsearch.Client({
    //config
    hosts: ["http://localhost:9200"],
    log: "trace"
});


visualize();





//----------------------------

function visualize() {
    //what to visualize
    var whatElement = document.getElementById("what");
    var what = whatElement.options[whatElement.selectedIndex].value;
    var whatText = whatElement.options[whatElement.selectedIndex].text;


    client.search({
        index: "d3gitindex",
        type: "commit",
        body: {
            "query": {
                "match_all" : {}
            },
            size: 0,
            "aggs": {
                "x": {
                    "date_histogram": {
                        "field": "date", //TODO generalize
                        "interval": "day"
                    },
                    "aggs": {
                        "y": {
                            "sum": {
                                "field": what
                            }
                        }
                    }
                }
            }
        }
    }, function (err, resp) {
        if (err) {
            console.trace(err.message);
        }


        var data = resp.aggregations.x.buckets;

        //clear the chart   TODO there must be a better way to do this
        chart.selectAll("g").remove();
        chart.selectAll(".bar").remove();


        x.domain(data.map(function(d) { return d.key_as_string.substring(0, 10); }));
        y.domain([0, d3.max(data, function(d) { return d.y.value; })]); //the second arg to max is an 'accessor function'

        chart.append("g")
            .attr("class", "x axis") //actually, two classes: 'x' and 'axis'
            .attr("transform", "translate(0," + chartHeight + ")") //the axis elements are positioned relative to a local origin
            .call(xAxis);

        chart.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Number of " + whatText);


        chart.selectAll(".bar")
            .data(data)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("x", function(d) { return x(d.key_as_string.substring(0, 10)); })
            .attr("y", function(d) { return y(d.y.value); })
            .attr("height", function(d) { return chartHeight - y(d.y.value); })
            .attr("width", x.rangeBand())
            .on('mouseover', tip.show)
            .on('mouseout', tip.hide);
    });
}
