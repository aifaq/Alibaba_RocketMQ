$(document).ready(function() {
    $("#registerButton").click(function() {
        var password = $("#password").val();
        var confirmPassword = $("#confirmPassword").val();

        var readyToRegister = true;

        if (password == "") {
            readyToRegister = false;
            alert("Password required");
        }

        if (password != confirmPassword) {
            readyToRegister = false;
            alert("Password and confirm password does not match");
        }

        var userName = $("#userName").val();

        if (userName == "") {
            readyToRegister = false;
            alert("User name required");
        }

        var email = $("#email").val();
        if (email == "") {
            readyToRegister = false;
            alert("Email required");
        }

        var teamId = $("#team").val();

        var data = {
            password: password,
            userName: userName,
            email: email,
            team: {id: teamId}
        };

        if(readyToRegister) {
            $.ajax({
                async: true,
                data: JSON.stringify(data),
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=UTF-8",
                url: "/cockpit/register",
                success: function(data) {
                    alert("Registration success");
                }
            });
        }

        return readyToRegister;
    });
});