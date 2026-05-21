Vercel deployment notes

- Build command: `npm run build`
- Output directory: `build`

Environment variables
- `REACT_APP_API_URL` : Set to your backend base URL (e.g. `https://api.example.com`). The app reads this via `process.env.REACT_APP_API_URL`.

Vercel project settings
- Create a new project in Vercel and link this repository (only the `frontend` folder needs to be deployed).
- In the Vercel import flow set the **Root Directory** to `frontend`.
- Confirm the build command and output directory above.
- Add `REACT_APP_API_URL` under Environment Variables for Production and Preview.

Notes
- The frontend uses client-side routing; `vercel.json` rewrites all routes to `index.html`.
- If your backend requires CORS, ensure it allows the Vercel deployment origin.
