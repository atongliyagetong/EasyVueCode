<!DOCTYPE html>
<html lang="en-US">
    <header>

    </header>
    <body >
            <h1>hello ${user.name}</h1>
            <form action="/newDoImport" id="fileupload" method="POST" enctype="multipart/form-data" >
                <input type="file" name="excel" >
                <input type="submit" >
            </form>
    </body>

</html>