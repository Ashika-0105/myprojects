import express from 'express';
const app = express();
const PORT = 3005;

app.get('/test', (req, res) => res.send("Server is working"));
app.get('/getall-files', (req, res) => res.send("All files route"));

app.listen(PORT, () => console.log(`Server on ${PORT}`));
