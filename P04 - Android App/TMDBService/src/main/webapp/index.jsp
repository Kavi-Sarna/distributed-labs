<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>TMDB API Service</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 40px;
                line-height: 1.6;
            }
            h1 {
                color: #2c75ea;
            }
            .container {
                max-width: 800px;
                margin: 0 auto;
            }
            .card {
                border: 1px solid #ddd;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                background-color: #f9f9f9;
            }
            a {
                color: #2c75ea;
                text-decoration: none;
            }
            a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Welcome to the TMDB Service</h1>
            <br/>
            <a href="https://developer.themoviedb.org/docs/getting-started">TMDB API Documentation</a>

            <div class="card">
                <h2>Service API Endpoints</h2>
                <p>This service provides access to TMDB APIs.</p>
                <ul>
                    <li><strong>GET /api/movies/:id</strong> - Get movie details</li>
                    <li><strong>GET /api/search/movies?query=YOUR_QUERY</strong> - Search movie by name</li>
                </ul>
            </div>

            <div class="card">
                <h2>Dashboard</h2>
                <p>View logs for the API service:</p>
                <p><a href="dashboard">Open Dashboard</a></p>
            </div>

        </div>
    </body>
</html>