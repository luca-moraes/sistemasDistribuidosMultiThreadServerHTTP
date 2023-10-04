function retangulo2() {
    var h = parseFloat(document.getElementById("altura").value);
    var b = parseFloat(document.getElementById("largura").value);

    var p = 2 * (h + b);
    var a = h * b;

    document.getElementById("perimetro").innerHTML = p;
    document.getElementById("area").innerHTML = a;
    document.getElementById("forma").style.height = h + "px";
    document.getElementById("forma").style.width = b + "px";
    document.getElementById("forma").style.backgroundColor = "blue";
}
