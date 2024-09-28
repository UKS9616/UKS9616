import React from 'react';

export function AboutView() {
    return (
        <div>
            <div>
                <div className="container py-4">
                    <header className="pb-3 mb-4 border-bottom">
                        <a href="createview.js" className="d-flex align-items-center text-body-emphasis text-decoration-none">
                            <span className="fs-4 text-dark-emphasis">SoleElegance</span>
                        </a>
                    </header>

                    <div className="p-5 mb-4 bg-body-tertiary rounded-3">
                        <div className="container-fluid py-5">
                            <h1 className="display-5 fw-bold">Class Information</h1>
                            <p className=" fs-4">SE/ComS319 "Construction of User Interface", Spring 2024. <br />Date: May 2nd, 2024 <br />Professor: Dr. Abraham N. Aldaco Gastelum <br />Email: aaldaco@iastate.edu</p>
                            <a href="createview.js"><button className="btn btn-primary btn-lg" type="button">Go To Home</button></a>
                        </div>
                    </div>

                    <div className="row align-items-md-stretch">
                        <div className="col-md-6">
                            <div className="h-100 p-5 bg-text-body-tertiary border rounded-3">
                                <h2>Student 1</h2>
                                <a href="https://ibb.co/Z81Z5B1"><img src="https://i.ibb.co/jMZpmyZ/IMG-3543.jpg" className="bd-placeholder-img rounded-circle float-end" width="140" height="140" alt="Student 1"></img></a>
                                
                                <p>Name: Prakarsha Poudel<br />Section: 03 <br />Class: SE319<br />Email: prak@iastate.edu</p>
                                <a href="createview.js"><button className="btn btn-outline-secondary" type="button">Go To Home</button></a>
                            </div>
                        </div>
                        <div className="col-md-6">
                            <div className="h-100 p-5 bg-text-body-secondary border rounded-3">
                                <h2>Student 2</h2>
                                <a href="https://ibb.co/BtqynZM"><img src="https://i.ibb.co/2ygvZt4/Image.jpg" className="bd-placeholder-img rounded-circle float-end" width="140" height="140" alt="Student 2" ></img></a>

                                <p>Name: Udip Shrestha <br/>Section: 03 <br />Class: SE319 <br />Email: udips@iastate.edu</p>
                                <a href="createview.js"><button className="btn btn-outline-secondary" type="button">Go To Home</button></a>
                            </div>
                        </div>
                    </div>

                    <footer className="pt-3 mt-4 text-body-secondary border-top">
                        &copy; 2023
                    </footer>
                </div>
            </div>

            <script src="../assets/dist/js/bootstrap.bundle.min.js"></script>
        </div>
    );
}
