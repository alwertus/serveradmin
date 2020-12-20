function text(textString) {
    let t = document.createTextNode(textString);
    return t;
}

function div(classname) {
    let d = document.createElement("DIV");
    d.className = classname;
    return d;
}

function formElement(action, buttonText, program, disabled = false) {
    let form = document.createElement("FORM");
    form.action = action;
    form.text = buttonText;
    form.program = program;
    form.method = "POST";
    form.addEventListener('submit', submitForm);

    let button = document.createElement("INPUT");
    if (disabled) button.className = "disabled";
    button.type = "submit";
    button.value=buttonText;

    form.appendChild(button);

    return form;
}

let rqHeaders = new Headers();
rqHeaders.append("Content-Type", "application/json;charset=utf-8");

function submitForm(event) {
    event.preventDefault();

    let request = new Request(event.target.action, {
        method: 'POST',
        body: JSON.stringify({
            "action" : event.target.text,
            "program" : event.target.program
        }),
        headers: rqHeaders,
    });

    fetch(request)
        .then(response => response.json())
        .then(
            function(response) {
                console.log(response);
                if (response.result === "OK") location.reload();
            },
            function(error) {
                console.error(error);
            })

}

function submitFormReloadCfg(event) {
    event.preventDefault();

    console.log(event.target.action);

    let request = new Request(event.target.action, {
        method: 'POST',
        body: JSON.stringify({"action":"reload"}),
        headers: rqHeaders,
    });

    fetch(request)
        .then(response => response.json())
        .then(
            function(response) {
                console.log(response);
            },
            function(error) {
                console.error(error);
            })
}

let formReloadCfg = document.getElementById("reloadcfgform");
formReloadCfg.addEventListener('submit', submitFormReloadCfg);

// fetch on load page
fetch("/api/v1/app/status", {
    method: "GET"
})
.then(response => response.json())
.then(data => {
    var sitecount = 0;

    data.map(item => {
        console.log(item)

        let divApp = div("siteline");
        divApp.id = "site" + (++sitecount);

        let divStatus = div("status " + (item.exists ? "active" : "die"));

        let divTitle = div("title");

        divTitle.appendChild(text(item.title));

        divApp.appendChild(divStatus);
        divApp.appendChild(divTitle);
        divApp.appendChild(formElement("/api/v1/app/action/start", "Start", item.title, item.exists));
        divApp.appendChild(formElement("/api/v1/app/action/stop", "Stop", item.title, !item.exists));
        document.getElementById("sitelist").appendChild(divApp);
    })
});